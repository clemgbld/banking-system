package app.netlify.clementgombauld.banking.account.unit;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.domain.exceptions.*;
import app.netlify.clementgombauld.banking.account.unit.inMemory.*;
import app.netlify.clementgombauld.banking.account.usecases.ReceiveMoneyFromExternalBank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReceiveMoneyFromExternalBankTest {

    public static final long CURRENT_DATE_IN_MS = 2534543253252L;
    public static final Instant CURRENT_DATE = Instant.ofEpochMilli(2534543253252L);
    public static final String TRANSACTION_ID = "2143";
    private IdGenerator idGenerator;
    private DateProvider dateProvider;
    private AccountRepository accountRepository;

    private BeneficiaryRepository beneficiaryRepository;


    @BeforeEach
    void setUp() {
        idGenerator = new InMemoryIdGenerator(List.of(TRANSACTION_ID));
        dateProvider = new InMemoryDateProvider(CURRENT_DATE_IN_MS);
        accountRepository = new InMemoryAccountRepository();
        beneficiaryRepository = new InMemoryBeneficiaryRepository();
    }

    @Test
    void shouldAddMoneyToTheExpectedBankAccountFromTheExternalBankWhenTheSenderAccountIsABeneficiary() {
        String accountId = "1";
        String receiverAccountIban = "FR1420041010050500013M02606";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR5030004000700000157389538";
        String accountName = "John Smith Junior";
        BigDecimal transactionAmount = new BigDecimal(5);
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName = "John Smith";
        String bic = "AGRIFRPP989";


        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(receiverAccountIban))
                .build()
        );

        beneficiaryRepository.insert(accountId, new Beneficiary(beneficiaryId, new Iban(beneficiaryIban), new Bic(beneficiaryBic), beneficiaryName));

        Map<String, Transaction> transactionStore = new HashMap<>();

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of(), Map.of(), transactionStore);

        receiveMoneyFromExternalBank.handle(receiverAccountIban, beneficiaryIban, beneficiaryBic, accountName, transactionAmount, bic);


        Account account = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);

        assertThat(account).isEqualTo(
                new Account.Builder()
                        .withId(accountId)
                        .withBalance(transactionAmount)
                        .withIban(new Iban(receiverAccountIban))
                        .build()

        );

        assertThat(transactionStore.get(accountId)).isEqualTo(new Transaction(TRANSACTION_ID, CURRENT_DATE, transactionAmount, beneficiaryIban, beneficiaryBic, beneficiaryName));
    }


    @Test
    void shouldAddMoneyToTheExpectedBankAccountFromTheExternalBankWhenTheSenderAccountHasADifferentCurrencyAndMakeTheConversion() {
        String accountId = "1";
        String receiverAccountIban = "FR1420041010050500013M02606";
        String senderAccountABARoutingNumber = "123456789";
        String senderAccountName = "John Smith Junior";
        BigDecimal transactionAmount = new BigDecimal(5);
        String transactionId = "2143";
        String senderAccountBic = "ACMEUS33123";
        String bic = "AGRIFRPP989";

        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(receiverAccountIban))
                .build()
        );

        Map<String, Transaction> transactionStore = new HashMap<>();

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of("US", "USD"), Map.of("USD", Map.of("EUR", new BigDecimal("0.88"))), transactionStore);

        receiveMoneyFromExternalBank.handle(receiverAccountIban, senderAccountABARoutingNumber, senderAccountBic, senderAccountName, transactionAmount, bic);


        Account account = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);

        assertThat(account).isEqualTo(
                new Account.Builder()
                        .withId(accountId)
                        .withBalance(new BigDecimal("4.40"))
                        .withIban(new Iban(receiverAccountIban))
                        .build()

        );

        assertThat(transactionStore.get(accountId)).isEqualTo(
                new Transaction(transactionId, CURRENT_DATE, new BigDecimal("4.40"), senderAccountABARoutingNumber, senderAccountBic, senderAccountName)
        );

    }

    @Test
    void shouldThrowAnExceptionWhenTheSenderAccountBicIsInvalid() {
        String accountId = "1";
        String receiverAccountIban = "FR1420041010050500013M02606";
        String senderAccountABARoutingNumber = "123456789";
        String senderAccountName = "John Smith Junior";
        BigDecimal transactionAmount = new BigDecimal(5);
        String senderAccountBic = "ACMEUS331";
        String bic = "AGRIFRPP989";

        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(receiverAccountIban))
                .build()
        );

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of(), Map.of(), Map.of());

        assertThatThrownBy(() -> receiveMoneyFromExternalBank.handle(receiverAccountIban, senderAccountABARoutingNumber, senderAccountBic, senderAccountName, transactionAmount, bic))
                .isInstanceOf(InvalidBicException.class)
                .hasMessage("bic: " + senderAccountBic + " is invalid.");
    }


    @Test
    void shouldThrowAnExceptionWhenTheCurrencyIsNotFoundForAGivenCountry() {
        String accountId = "1";
        String receiverAccountIban = "FR1420041010050500013M02606";
        String senderAccountABARoutingNumber = "123456789";
        String senderAccountName = "John Smith Junior";
        BigDecimal transactionAmount = new BigDecimal(5);
        String senderAccountBic = "ACMEUS33123";
        String bic = "AGRIFRPP989";

        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(receiverAccountIban))
                .build()
        );

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of(), Map.of(), Map.of());
        assertThatThrownBy(() -> receiveMoneyFromExternalBank.handle(receiverAccountIban, senderAccountABARoutingNumber, senderAccountBic, senderAccountName, transactionAmount, bic))
                .isInstanceOf(CurrencyNotFoundException.class)
                .hasMessage("No Currency found for country code : US.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheExchangeRateIsNotFoundForaGivenCurrency() {
        String accountId = "1";
        String receiverAccountIban = "FR1420041010050500013M02606";
        String senderAccountABARoutingNumber = "123456789";
        String senderAccountName = "John Smith Junior";
        BigDecimal transactionAmount = new BigDecimal(5);
        String senderAccountBic = "ACMEUS33123";
        String bic = "AGRIFRPP989";

        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(receiverAccountIban))
                .build()
        );

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of("US", "USD"), Map.of(), Map.of());
        assertThatThrownBy(() -> receiveMoneyFromExternalBank.handle(receiverAccountIban, senderAccountABARoutingNumber, senderAccountBic, senderAccountName, transactionAmount, bic))
                .isInstanceOf(ExchangeRateNotFound.class)
                .hasMessage("No Exchange Rate found for this currency : USD.");

    }

    @Test
    void shouldThrowAnExceptionWhenTheSenderAccountIsNoFromAnExternalBank() {
        String accountId = "1";
        String receiverAccountIban = "FR1420041010050500013M02606";
        String senderAccountABARoutingNumber = "123456789";
        String senderAccountName = "John Smith Junior";
        BigDecimal transactionAmount = new BigDecimal(5);
        String senderAccountBic = "AGRIFRPP989";
        String bic = "AGRIFRPP989";

        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(receiverAccountIban))
                .build()
        );

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of("US", "USD"), Map.of(), Map.of());
        assertThatThrownBy(() -> receiveMoneyFromExternalBank.handle(receiverAccountIban, senderAccountABARoutingNumber, senderAccountBic, senderAccountName, transactionAmount, bic))
                .isInstanceOf(SameBankException.class)
                .hasMessage("Your account does not belong to an external bank.");

    }

    @Test
    void shouldThrowAnExceptionWhenThereIsNoReceiverAccount() {
        String receiverAccountIban = "FR1420041010050500013M02606";
        String senderAccountABARoutingNumber = "123456789";
        String senderAccountName = "John Smith Junior";
        BigDecimal transactionAmount = new BigDecimal(5);
        String senderAccountBic = "DEUTDEFF";
        String bic = "AGRIFRPP989";

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of(), Map.of(), Map.of());
        assertThatThrownBy(() -> receiveMoneyFromExternalBank.handle(receiverAccountIban, senderAccountABARoutingNumber, senderAccountBic, senderAccountName, transactionAmount, bic))
                .isInstanceOf(UnknownAccountWithIbanException.class)
                .hasMessage("There is no account with the accountIdentifier: " + receiverAccountIban);
    }

    @Test
    void shouldNotPerformAnyCurrencyConversionWhenTheRetrievedCurrencyIsTheSameThanTheBankCurrency() {
        String accountId = "1";
        String receiverAccountIban = "FR1420041010050500013M02606";
        String senderAccountABARoutingNumber = "123456789";
        String senderAccountName = "John Smith Junior";
        BigDecimal transactionAmount = new BigDecimal(5);
        String transactionId = "2143";
        String senderAccountBic = "DEUTDEFF";
        String bic = "AGRIFRPP989";


        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(receiverAccountIban))
                .build()
        );

        Map<String, Transaction> transactionStore = new HashMap<>();

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of("DE", "EUR"), Map.of(), transactionStore);

        receiveMoneyFromExternalBank.handle(receiverAccountIban, senderAccountABARoutingNumber, senderAccountBic, senderAccountName, transactionAmount, bic);

        Account account = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);

        assertThat(account).isEqualTo(
                new Account.Builder()
                        .withId(accountId)
                        .withBalance(transactionAmount)
                        .withIban(new Iban(receiverAccountIban))
                        .build());
        assertThat(transactionStore.get(accountId)).isEqualTo(new Transaction(transactionId, CURRENT_DATE, transactionAmount, senderAccountABARoutingNumber, senderAccountBic, senderAccountName));
    }

    private ReceiveMoneyFromExternalBank buildReceiveMoneyFromExternalBank(Map<String, String> countryCodeToCurrency, Map<String, Map<String, BigDecimal>> exchangeRateStore, Map<String, Transaction> transactionStore) {

        CountryGateway countryGateway = new InMemoryCountryGateway(countryCodeToCurrency);

        CurrencyGateway currencyGateway = new InMemoryCurrencyGateway(exchangeRateStore);

        TransactionRepository transactionRepository = new InMemoryTransactionRepository(transactionStore);

        return new ReceiveMoneyFromExternalBank(accountRepository, transactionRepository, beneficiaryRepository, idGenerator, dateProvider, countryGateway, currencyGateway);
    }
}