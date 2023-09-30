package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.ExchangeRateNotFound;
import app.netlify.clementgombauld.banking.core.domain.exceptions.InvalidBicException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.CurrencyNotFoundException;
import app.netlify.clementgombauld.banking.infra.inMemory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.Instant;
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


    @BeforeEach
    void setUp() {
        idGenerator = new InMemoryIdGenerator(List.of(TRANSACTION_ID));
        dateProvider = new InMemoryDateProvider(CURRENT_DATE_IN_MS);
        accountRepository = new InMemoryAccountRepository();
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


        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(receiverAccountIban)
                .withBeneficiaries(List.of(new Beneficiary(beneficiaryId, beneficiaryIban, beneficiaryBic, beneficiaryName)))
                .build()
        );

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of(), Map.of());

        receiveMoneyFromExternalBank.handle(receiverAccountIban, beneficiaryIban, beneficiaryBic, accountName, transactionAmount);


        Account account = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);

        assertThat(account).usingRecursiveComparison().isEqualTo(
                new Account.Builder()
                        .withId(accountId)
                        .withBalance(transactionAmount)
                        .withIban(receiverAccountIban)
                        .withBeneficiaries(List.of(new Beneficiary(beneficiaryId, beneficiaryIban, beneficiaryBic, beneficiaryName)))
                        .withTransactions(List.of(new MoneyTransferred(TRANSACTION_ID, CURRENT_DATE, transactionAmount, beneficiaryIban, beneficiaryBic, beneficiaryName)))
                        .build()

        );
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

        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(receiverAccountIban)
                .build()
        );

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of("US", "USD"), Map.of("USD", Map.of("EUR", new BigDecimal("0.88"))));

        receiveMoneyFromExternalBank.handle(receiverAccountIban, senderAccountABARoutingNumber, senderAccountBic, senderAccountName, transactionAmount);


        Account account = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);

        assertThat(account).usingRecursiveComparison().isEqualTo(
                new Account.Builder()
                        .withId(accountId)
                        .withBalance(new BigDecimal("4.40"))
                        .withIban(receiverAccountIban)
                        .withTransactions(List.of(new MoneyTransferred(transactionId, CURRENT_DATE, new BigDecimal("4.40"), senderAccountABARoutingNumber, senderAccountBic, senderAccountName)))
                        .build()

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

        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(receiverAccountIban)
                .build()
        );

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of(), Map.of());

        assertThatThrownBy(() -> receiveMoneyFromExternalBank.handle(receiverAccountIban, senderAccountABARoutingNumber, senderAccountBic, senderAccountName, transactionAmount))
                .isInstanceOf(InvalidBicException.class)
                .hasMessage("bic: " + senderAccountBic + " is invalid.");
    }

    private ReceiveMoneyFromExternalBank buildReceiveMoneyFromExternalBank(Map<String, String> countryCodeToCurrency, Map<String, Map<String, BigDecimal>> exchangeRateStore) {

        CountryGateway countryGateway = new InMemoryCountryGateway(countryCodeToCurrency);

        CurrencyGateway currencyGateway = new InMemoryCurrencyGateway(exchangeRateStore);

        return new ReceiveMoneyFromExternalBank(accountRepository, idGenerator, dateProvider, countryGateway, currencyGateway);
    }

    @Test
    void shouldThrowAnExceptionWhenTheCurrencyIsNotFoundForAGivenCountry() {
        String accountId = "1";
        String receiverAccountIban = "FR1420041010050500013M02606";
        String senderAccountABARoutingNumber = "123456789";
        String senderAccountName = "John Smith Junior";
        BigDecimal transactionAmount = new BigDecimal(5);
        String senderAccountBic = "ACMEUS33123";

        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(receiverAccountIban)
                .build()
        );

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of(), Map.of());
        assertThatThrownBy(() -> receiveMoneyFromExternalBank.handle(receiverAccountIban, senderAccountABARoutingNumber, senderAccountBic, senderAccountName, transactionAmount))
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

        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(receiverAccountIban)
                .build()
        );

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = buildReceiveMoneyFromExternalBank(Map.of("US", "USD"), Map.of());
        assertThatThrownBy(() -> receiveMoneyFromExternalBank.handle(receiverAccountIban, senderAccountABARoutingNumber, senderAccountBic, senderAccountName, transactionAmount))
                .isInstanceOf(ExchangeRateNotFound.class)
                .hasMessage("No Exchange Rate found for this currency : USD.");

    }
}