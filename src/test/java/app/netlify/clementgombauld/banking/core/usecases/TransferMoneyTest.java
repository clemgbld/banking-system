package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.*;
import app.netlify.clementgombauld.banking.infra.inMemory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class TransferMoneyTest {

    private AuthenticationGateway authenticationGateway;

    private AccountRepository accountRepository;

    private DateProvider dateProvider;

    @BeforeEach
    void setUp() {
        this.authenticationGateway = new InMemoryAuthenticationGateway();
        this.accountRepository = new InMemoryAccountRepository();
        this.dateProvider = new InMemoryDateProvider(1631000000000L);
    }

    @Test
    void shouldPerformAMoneyTransferBetweenTwoAccountsInTheSameBank() {
        String customerId = "1345";
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR5030004000700000157389538";
        String bic = "AGRIFRPP989";
        String receiverAccountBIC = "AGRIFRPP989";
        String senderAccountId = "1";
        String receiverAccountId = "2";
        String senderTransactionId = "13543A";
        String receiverTransactionId = "143E53245";
        String senderAccountFirstName = "Paul";
        String senderAccountLastName = "Duboit";
        String receiverAccountFirstName = "John";
        String receiverAccountLastName = "Smith";
        BigDecimal transactionAmount = new BigDecimal(5);

        Customer currentCustomer = new Customer(customerId, senderAccountFirstName, senderAccountLastName);
        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(senderAccountIban)
                .withBalance(new BigDecimal(105))
                .withTransactions(new ArrayList<>(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName))))
                .withBeneficiaries(List.of(new Beneficiary("AE434", receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName)))
                .withCustomer(currentCustomer)
                .build();

        currentCustomer.addAccount(existingSenderAccount);

        Instant currentInstant = dateProvider.now();
        accountRepository.save(new Account.Builder()
                .withId(receiverAccountId)
                .withIban(receiverAccountIban)
                .withBalance(new BigDecimal(100))
                .withTransactions(null)
                .withBeneficiaries(List.of())
                .build());

        authenticationGateway.authenticate(currentCustomer);


        TransferMoney transferMoney = buildTransferMoney(List.of(senderTransactionId, receiverTransactionId), List.of(), List.of());

        transferMoney.handle(transactionAmount, receiverAccountIban, bic);

        Account senderAccount = accountRepository.findByIban(senderAccountIban).orElseThrow(RuntimeException::new);
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);

        assertThat(senderAccount).usingRecursiveComparison().isEqualTo(new Account.Builder()
                .withId(senderAccountId)
                .withIban(senderAccountIban)
                .withBalance(new BigDecimal(100))
                .withTransactions(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName), new MoneyTransferred(senderTransactionId, currentInstant, new BigDecimal(-5), receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName)))
                .withBeneficiaries(List.of(new Beneficiary("AE434", receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName)))
                .withCustomer(currentCustomer)
                .build());

        assertThat(receiverAccount).usingRecursiveComparison().isEqualTo(new Account.Builder()
                .withId(receiverAccountId)
                .withIban(receiverAccountIban)
                .withBalance(new BigDecimal(105))
                .withTransactions(List.of(new MoneyTransferred(receiverTransactionId, currentInstant, new BigDecimal(5), senderAccountIban, bic, senderAccountFirstName + " " + senderAccountLastName)))
                .withBeneficiaries(List.of())
                .build());
    }


    @Test
    void shouldPerformAMoneyTransferBetweenTwoAccountsThatAreNotInTheSameBank() {
        String customerId = "1345";
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR5030004000700000157389538";
        String bic = "AGRIFRPP989";
        String receiverAccountBIC = "BNPAFRPP123";
        String senderAccountId = "1";
        String senderTransactionId = "13543A";
        String receiverTransactionId = "143E53245";
        String senderAccountFirstName = "Paul";
        String senderAccountLastName = "Duboit";
        String receiverAccountFirstName = "John";
        String receiverAccountLastName = "Smith";


        BigDecimal transactionAmount = new BigDecimal(5);

        Customer currentCustomer = new Customer(customerId, senderAccountFirstName, senderAccountLastName);

        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(senderAccountIban)
                .withBalance(new BigDecimal(105))
                .withTransactions(new ArrayList<>(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName))))
                .withBeneficiaries(List.of(new Beneficiary("AE434", receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName)))
                .withCustomer(currentCustomer)
                .build();

        currentCustomer.addAccount(existingSenderAccount);

        authenticationGateway.authenticate(currentCustomer);

        Instant currentInstant = dateProvider.now();

        List<MoneyTransferred> extraBankTransactions = new ArrayList<>();

        List<String> extraBankAccountInfos = new ArrayList<>();

        TransferMoney transferMoney = buildTransferMoney(List.of(senderTransactionId, receiverTransactionId), extraBankTransactions, extraBankAccountInfos);

        transferMoney.handle(transactionAmount, receiverAccountIban, bic);

        Account senderAccount = accountRepository.findByIban(senderAccountIban).orElseThrow(RuntimeException::new);

        assertThat(senderAccount)
                .usingRecursiveComparison()
                .isEqualTo(new Account.Builder()
                        .withId(senderAccountId)
                        .withIban(senderAccountIban)
                        .withBalance(new BigDecimal(100))
                        .withTransactions(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName), new MoneyTransferred(senderTransactionId, currentInstant, new BigDecimal(-5), receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName)))
                        .withBeneficiaries(List.of(new Beneficiary("AE434", receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName)))
                        .withCustomer(currentCustomer)
                        .build());

        assertThat(extraBankTransactions).usingRecursiveComparison().isEqualTo(List.of(new MoneyTransferred(receiverTransactionId, currentInstant, new BigDecimal(5), senderAccountIban, bic, senderAccountFirstName + " " + senderAccountLastName)));
        assertThat(extraBankAccountInfos).usingRecursiveComparison().isEqualTo(List.of(receiverAccountIban, receiverAccountBIC));
    }


    @Test
    void shouldThrowAnExceptionWhenTheReceiverAccountIsNotIsNotInTheBeneficiariesListOfTheSenderAccount() {
        String customerId = "1345";
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR3429051014050500014M02606";
        String bic = "AGRIFRPP989";
        String senderAccountId = "1";
        String receiverAccountId = "2";
        String senderTransactionId = "13543A";
        String receiverTransactionId = "143E53245";
        String senderAccountFirstName = "Paul";
        String senderAccountLastName = "Duboit";


        BigDecimal transactionAmount = new BigDecimal(5);

        Customer currentCustomer = new Customer(customerId, senderAccountFirstName, senderAccountLastName);
        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(senderAccountIban)
                .withBalance(new BigDecimal(105))
                .withTransactions(List.of())
                .withBeneficiaries(List.of())
                .withCustomer(currentCustomer)
                .build();
        currentCustomer.addAccount(existingSenderAccount);

        authenticationGateway.authenticate(currentCustomer);

        accountRepository.save(new Account.Builder()
                .withId(receiverAccountId)
                .withIban(receiverAccountIban)
                .withBalance(new BigDecimal(100))
                .withBeneficiaries(List.of())
                .build());


        TransferMoney transferMoney = buildTransferMoney(List.of(senderTransactionId, receiverTransactionId), List.of(), List.of());

        assertThatThrownBy(() -> transferMoney.handle(transactionAmount, receiverAccountIban, bic)).isInstanceOf(UnknownBeneficiaryException.class)
                .hasMessage("Cannot find any account with the accountIdentifier: " + receiverAccountIban + " in your beneficiaries list.");
    }

    @Test
    void shouldThrowAnExceptionWhenThereIsNoCurrentCustomer() {
        String receiverAccountIban = "FR5030004000700000157389538";
        String bic = "AGRIFRPP989";

        TransferMoney transferMoney = buildTransferMoney(List.of(), List.of(), List.of());

        assertThatThrownBy(() -> transferMoney.handle(new BigDecimal(100), receiverAccountIban, bic))
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheBicOfTheBankIsInvalid() {
        String receiverAccountIban = "FR5030004000700000157389538";
        String bic = "invalidBic";

        TransferMoney transferMoney = buildTransferMoney(List.of(), List.of(), List.of());
        assertThatThrownBy(() -> transferMoney.handle(new BigDecimal(100), receiverAccountIban, bic))
                .isInstanceOf(InvalidBicException.class)
                .hasMessage("bic: " + bic + " is invalid.");

    }


    @Test
    void shouldThrowAnExceptionWhenTheSenderAccountBalanceIsInsufficient() {
        String customerId = "1345";
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR5030004000700000157389538";
        String bic = "AGRIFRPP989";
        String senderAccountId = "1";
        String receiverAccountId = "2";
        String senderTransactionId = "13543A";
        String receiverTransactionId = "143E53245";
        String senderAccountFirstName = "Paul";
        String senderAccountLastName = "Duboit";
        String receiverAccountFirstName = "John";
        String receiverAccountLastName = "Smith";

        BigDecimal transactionAmount = new BigDecimal(1000);

        Customer currentCustomer = new Customer(customerId, senderAccountFirstName, senderAccountLastName);

        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(senderAccountIban)
                .withBalance(new BigDecimal(105))
                .withTransactions(new ArrayList<>(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban, bic, receiverAccountFirstName + " " + receiverAccountLastName))))
                .withBeneficiaries(List.of(new Beneficiary("AE434", receiverAccountIban, bic, receiverAccountFirstName + " " + receiverAccountLastName)))
                .withCustomer(currentCustomer)
                .build();
        currentCustomer.addAccount(existingSenderAccount);

        authenticationGateway.authenticate(currentCustomer);

        accountRepository.save(new Account.Builder()
                .withId(receiverAccountId)
                .withIban(receiverAccountIban)
                .withBalance(new BigDecimal(100))
                .withTransactions(null)
                .withBeneficiaries(List.of())
                .build());

        TransferMoney transferMoney = buildTransferMoney(List.of(senderTransactionId, receiverTransactionId), List.of(), List.of());

        assertThatThrownBy(() -> transferMoney.handle(transactionAmount, receiverAccountIban, bic)).isInstanceOf(InsufficientBalanceException.class);
    }


    @Test
    void shouldThrowAnExceptionWhenTheReceiverAccountDoesNotExists() {
        String customerId = "1345";
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR5030004000700000157389538";
        String bic = "AGRIFRPP989";
        String senderAccountId = "1";
        String senderTransactionId = "13543A";
        String receiverTransactionId = "143E53245";
        String senderAccountFirstName = "Paul";
        String senderAccountLastName = "Duboit";
        String receiverAccountFirstName = "John";
        String receiverAccountLastName = "Smith";
        BigDecimal transactionAmount = new BigDecimal(5);

        Customer currentCustomer = new Customer(customerId, senderAccountFirstName, senderAccountLastName);

        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(senderAccountIban)
                .withBalance(new BigDecimal(105))
                .withTransactions(new ArrayList<>(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban, bic, receiverAccountFirstName + " " + receiverAccountLastName))))
                .withBeneficiaries(List.of(new Beneficiary("AE434", receiverAccountIban, bic, receiverAccountFirstName + " " + receiverAccountLastName)))
                .withCustomer(currentCustomer)
                .build();

        currentCustomer.addAccount(existingSenderAccount);

        authenticationGateway.authenticate(currentCustomer);

        TransferMoney transferMoney = buildTransferMoney(List.of(senderTransactionId, receiverTransactionId), List.of(), List.of());

        assertThatThrownBy(() -> transferMoney.handle(transactionAmount, receiverAccountIban, bic)).isInstanceOf(UnknownAccountWithIbanException.class)
                .hasMessage("There is no account with the accountIdentifier: " + receiverAccountIban);
    }


    private TransferMoney buildTransferMoney(List<String> ids, List<MoneyTransferred> capturedTransactions, List<String> capturedAccountInfos) {
        IdGenerator idGenerator = new InMemoryIdGenerator(ids);
        ExtraBankTransactionsGateway extraBankTransactionsGateway = new InMemoryExtraBankTransactionsGateway(capturedTransactions, capturedAccountInfos);
        return new TransferMoney(accountRepository, dateProvider, idGenerator, extraBankTransactionsGateway, authenticationGateway);
    }
}