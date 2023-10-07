package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.*;
import app.netlify.clementgombauld.banking.infra.inMemory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class TransferMoneyTest {

    private AuthenticationGateway authenticationGateway;

    private BeneficiaryRepository beneficiaryRepository;

    private DateProvider dateProvider;

    @BeforeEach
    void setUp() {
        this.authenticationGateway = new InMemoryAuthenticationGateway();
        this.beneficiaryRepository = new InMemoryBeneficiaryRepository();
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

        Map<String, Account> accountStore = new HashMap<>();
        Customer currentCustomer = new Customer(customerId, senderAccountFirstName, senderAccountLastName);
        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(new Iban(senderAccountIban))
                .withBalance(new BigDecimal(105))
                .build();

        accountStore.put(customerId, existingSenderAccount);


        Instant currentInstant = dateProvider.now();

        accountStore.put(receiverAccountIban, new Account.Builder()
                .withId(receiverAccountId)
                .withIban(new Iban(receiverAccountIban))
                .withBalance(new BigDecimal(100))
                .build());

        authenticationGateway.authenticate(currentCustomer);

        Map<String, Transaction> transactionStore = new HashMap<>();

        beneficiaryRepository.insert(senderAccountId, new Beneficiary("AE434", receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName));

        TransferMoney transferMoney = buildTransferMoney(accountStore, List.of(senderTransactionId, receiverTransactionId), List.of(), List.of(), transactionStore);

        transferMoney.handle(transactionAmount, receiverAccountIban, bic);

        Account senderAccount = accountStore.get(senderAccountIban);
        Account receiverAccount = accountStore.get(receiverAccountIban);

        assertThat(senderAccount).usingRecursiveComparison().isEqualTo(new Account.Builder()
                .withId(senderAccountId)
                .withIban(new Iban(senderAccountIban))
                .withBalance(new BigDecimal(100))
                .build());

        assertThat(transactionStore.get(senderAccountId)).isEqualTo(new Transaction(senderTransactionId, currentInstant, new BigDecimal(-5), receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName));

        assertThat(receiverAccount)
                .isEqualTo(new Account.Builder()
                        .withId(receiverAccountId)
                        .withIban(new Iban(receiverAccountIban))
                        .withBalance(new BigDecimal(105))
                        .build());

        assertThat(transactionStore.get(receiverAccountId)).isEqualTo(new Transaction(receiverTransactionId, currentInstant, new BigDecimal(5), senderAccountIban, bic, senderAccountFirstName + " " + senderAccountLastName));

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

        Map<String, Account> accountStore = new HashMap<>();

        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(new Iban(senderAccountIban))
                .withBalance(new BigDecimal(105))
                .build();

        accountStore.put(customerId, existingSenderAccount);


        authenticationGateway.authenticate(currentCustomer);

        Instant currentInstant = dateProvider.now();

        List<Transaction> extraBankTransactions = new ArrayList<>();

        List<String> extraBankAccountInfos = new ArrayList<>();

        Map<String, Transaction> transactionStore = new HashMap<>();

        beneficiaryRepository.insert(senderAccountId, new Beneficiary("AE434", receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName));

        TransferMoney transferMoney = buildTransferMoney(accountStore, List.of(senderTransactionId, receiverTransactionId), extraBankTransactions, extraBankAccountInfos, transactionStore);

        transferMoney.handle(transactionAmount, receiverAccountIban, bic);

        Account senderAccount = accountStore.get(senderAccountIban);

        assertThat(senderAccount)
                .isEqualTo(new Account.Builder()
                        .withId(senderAccountId)
                        .withIban(new Iban(senderAccountIban))
                        .withBalance(new BigDecimal(100))
                        .build());

        assertThat(transactionStore.get(senderAccountId))
                .isEqualTo(new Transaction(senderTransactionId, currentInstant, new BigDecimal(-5), receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName));

        assertThat(extraBankTransactions).usingRecursiveComparison().isEqualTo(List.of(new Transaction(receiverTransactionId, currentInstant, new BigDecimal(5), senderAccountIban, bic, senderAccountFirstName + " " + senderAccountLastName)));
        assertThat(extraBankAccountInfos).usingRecursiveComparison().isEqualTo(List.of(receiverAccountIban, receiverAccountBIC));
    }


    @Test
    void shouldThrowAnExceptionWhenTheReceiverAccountIsNotIsNotInTheBeneficiariesListOfTheSenderAccount() {
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


        BigDecimal transactionAmount = new BigDecimal(5);

        Map<String, Account> accountStore = new HashMap<>();


        Customer currentCustomer = new Customer(customerId, senderAccountFirstName, senderAccountLastName);
        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(new Iban(senderAccountIban))
                .withBalance(new BigDecimal(105))
                .build();
        accountStore.put(customerId, existingSenderAccount);

        authenticationGateway.authenticate(currentCustomer);


        accountStore.put(receiverAccountIban, new Account.Builder()
                .withId(receiverAccountId)
                .withIban(new Iban(receiverAccountIban))
                .withBalance(new BigDecimal(100))
                .build());


        TransferMoney transferMoney = buildTransferMoney(accountStore, List.of(senderTransactionId, receiverTransactionId), List.of(), List.of(), Map.of());

        assertThatThrownBy(() -> transferMoney.handle(transactionAmount, receiverAccountIban, bic)).isInstanceOf(UnknownBeneficiaryException.class)
                .hasMessage("Cannot find any account with the accountIdentifier: " + receiverAccountIban + " in your beneficiaries list.");
    }

    @Test
    void shouldThrowAnExceptionWhenThereIsNoCurrentCustomer() {
        String receiverAccountIban = "FR5030004000700000157389538";
        String bic = "AGRIFRPP989";

        TransferMoney transferMoney = buildTransferMoney(new HashMap<>(), List.of(), List.of(), List.of(), Map.of());

        assertThatThrownBy(() -> transferMoney.handle(new BigDecimal(100), receiverAccountIban, bic))
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheBicOfTheBankIsInvalid() {
        String receiverAccountIban = "FR5030004000700000157389538";
        String bic = "invalidBic";

        TransferMoney transferMoney = buildTransferMoney(new HashMap<>(), List.of(), List.of(), List.of(), Map.of());
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
        Map<String, Account> accountStore = new HashMap<>();

        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(new Iban(senderAccountIban))
                .withBalance(new BigDecimal(105))
                .build();

        accountStore.put(customerId, existingSenderAccount);


        authenticationGateway.authenticate(currentCustomer);


        accountStore.put(receiverAccountIban, new Account.Builder()
                .withId(receiverAccountId)
                .withIban(new Iban(receiverAccountIban))
                .withBalance(new BigDecimal(100))
                .build());

        beneficiaryRepository.insert(senderAccountId, new Beneficiary("AE434", receiverAccountIban, bic, receiverAccountFirstName + " " + receiverAccountLastName));

        TransferMoney transferMoney = buildTransferMoney(accountStore, List.of(senderTransactionId, receiverTransactionId), List.of(), List.of(), Map.of());

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

        HashMap<String, Account> accountStore = new HashMap<>();

        Customer currentCustomer = new Customer(customerId, senderAccountFirstName, senderAccountLastName);

        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(new Iban(senderAccountIban))
                .withBalance(new BigDecimal(105))
                .build();

        accountStore.put(customerId, existingSenderAccount);


        authenticationGateway.authenticate(currentCustomer);

        beneficiaryRepository.insert(senderAccountId, new Beneficiary("AE434", receiverAccountIban, bic, receiverAccountFirstName + " " + receiverAccountLastName));


        TransferMoney transferMoney = buildTransferMoney(accountStore, List.of(senderTransactionId, receiverTransactionId), List.of(), List.of(), new HashMap<>());

        assertThatThrownBy(() -> transferMoney.handle(transactionAmount, receiverAccountIban, bic)).isInstanceOf(UnknownAccountWithIbanException.class)
                .hasMessage("There is no account with the accountIdentifier: " + receiverAccountIban);
    }

    @Test
    void shouldThrowAnExceptionWhenTheSenderAccountDoesNotExists() {
        String customerId = "1345";
        String receiverAccountIban = "FR5030004000700000157389538";
        String bic = "AGRIFRPP989";
        String senderTransactionId = "13543A";
        String receiverTransactionId = "143E53245";
        String senderAccountFirstName = "Paul";
        String senderAccountLastName = "Duboit";
        BigDecimal transactionAmount = new BigDecimal(5);

        Customer currentCustomer = new Customer(customerId, senderAccountFirstName, senderAccountLastName);
        authenticationGateway.authenticate(currentCustomer);
        TransferMoney transferMoney = buildTransferMoney(new HashMap<>(), List.of(senderTransactionId, receiverTransactionId), List.of(), List.of(), new HashMap<>());
        assertThatThrownBy(() -> transferMoney.handle(transactionAmount, receiverAccountIban, bic)).isInstanceOf(UnknownAccountWithCustomerId.class)
                .hasMessage("There is no account with the customerId: " + customerId);

    }


    private TransferMoney buildTransferMoney(Map<String, Account> accountStore, List<String> ids, List<Transaction> capturedTransactions, List<String> capturedAccountInfos, Map<String, Transaction> transactionStore) {
        TransactionRepository transactionRepository = new InMemoryTransactionRepository(transactionStore);
        IdGenerator idGenerator = new InMemoryIdGenerator(ids);
        ExternalBankTransactionsGateway externalBankTransactionsGateway = new InMemoryExternalBankTransactionsGateway(capturedTransactions, capturedAccountInfos);
        AccountRepository accountRepository = new InMemoryAccountRepository(accountStore);
        return new TransferMoney(accountRepository, beneficiaryRepository, transactionRepository, dateProvider, idGenerator, externalBankTransactionsGateway, authenticationGateway);

    }
}