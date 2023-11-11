package app.netlify.clementgombauld.banking.account.unit.usecases.commands;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.domain.exceptions.*;
import app.netlify.clementgombauld.banking.account.unit.inmemory.*;
import app.netlify.clementgombauld.banking.account.usecases.commands.TransferMoney;
import app.netlify.clementgombauld.banking.account.usecases.commands.TransferMoneyCommand;
import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import app.netlify.clementgombauld.banking.common.domain.IdGenerator;
import app.netlify.clementgombauld.banking.common.inmemory.DeterministicDateProvider;
import app.netlify.clementgombauld.banking.common.inmemory.InMemoryIdGenerator;
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


    private BeneficiaryRepository beneficiaryRepository;

    private DateProvider dateProvider;

    @BeforeEach
    void setUp() {
        this.beneficiaryRepository = new InMemoryBeneficiaryRepository();
        this.dateProvider = new DeterministicDateProvider(1631000000000L);
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
        String reason = "a reason";

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


        Map<String, Transaction> transactionStore = new HashMap<>();

        beneficiaryRepository.insert(senderAccountId, new Beneficiary("AE434", new Iban(receiverAccountIban), new Bic(receiverAccountBIC), receiverAccountFirstName + " " + receiverAccountLastName, new Iban(
                senderAccountIban
        )));

        TransferMoney transferMoney = buildTransferMoney(accountStore, List.of(senderTransactionId, receiverTransactionId), List.of(), List.of(), transactionStore, currentCustomer);

        transferMoney.handle(new TransferMoneyCommand(transactionAmount, receiverAccountIban, bic, reason));

        Account senderAccount = accountStore.get(senderAccountIban);
        Account receiverAccount = accountStore.get(receiverAccountIban);

        assertThat(senderAccount).usingRecursiveComparison().isEqualTo(new Account.Builder()
                .withId(senderAccountId)
                .withIban(new Iban(senderAccountIban))
                .withBalance(new BigDecimal(100))
                .build());

        assertThat(transactionStore.get(senderAccountId)).isEqualTo(new Transaction(senderTransactionId, currentInstant, new BigDecimal(-5), receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName, reason));

        assertThat(receiverAccount)
                .isEqualTo(new Account.Builder()
                        .withId(receiverAccountId)
                        .withIban(new Iban(receiverAccountIban))
                        .withBalance(new BigDecimal(105))
                        .build());

        assertThat(transactionStore.get(receiverAccountId)).isEqualTo(new Transaction(receiverTransactionId, currentInstant, new BigDecimal(5), senderAccountIban, bic, senderAccountFirstName + " " + senderAccountLastName, reason));

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
        String reason = "a reason";


        BigDecimal transactionAmount = new BigDecimal(5);

        Customer currentCustomer = new Customer(customerId, senderAccountFirstName, senderAccountLastName);

        Map<String, Account> accountStore = new HashMap<>();

        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(new Iban(senderAccountIban))
                .withBalance(new BigDecimal(105))
                .build();

        accountStore.put(customerId, existingSenderAccount);

        Instant currentInstant = dateProvider.now();

        List<Transaction> extraBankTransactions = new ArrayList<>();

        List<String> extraBankAccountInfos = new ArrayList<>();

        Map<String, Transaction> transactionStore = new HashMap<>();

        beneficiaryRepository.insert(senderAccountId, new Beneficiary("AE434", new Iban(receiverAccountIban), new Bic(receiverAccountBIC), receiverAccountFirstName + " " + receiverAccountLastName, new Iban(senderAccountIban)));

        TransferMoney transferMoney = buildTransferMoney(accountStore, List.of(senderTransactionId, receiverTransactionId), extraBankTransactions, extraBankAccountInfos, transactionStore, currentCustomer);

        transferMoney.handle(new TransferMoneyCommand(transactionAmount, receiverAccountIban, bic, reason));

        Account senderAccount = accountStore.get(senderAccountIban);

        assertThat(senderAccount)
                .isEqualTo(new Account.Builder()
                        .withId(senderAccountId)
                        .withIban(new Iban(senderAccountIban))
                        .withBalance(new BigDecimal(100))
                        .build());

        assertThat(transactionStore.get(senderAccountId))
                .isEqualTo(new Transaction(senderTransactionId, currentInstant, new BigDecimal(-5), receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName, reason));

        assertThat(extraBankTransactions).usingRecursiveComparison().isEqualTo(List.of(new Transaction(receiverTransactionId, currentInstant, new BigDecimal(5), senderAccountIban, bic, senderAccountFirstName + " " + senderAccountLastName, reason)));
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

        accountStore.put(receiverAccountIban, new Account.Builder()
                .withId(receiverAccountId)
                .withIban(new Iban(receiverAccountIban))
                .withBalance(new BigDecimal(100))
                .build());


        TransferMoney transferMoney = buildTransferMoney(accountStore, List.of(senderTransactionId, receiverTransactionId), List.of(), List.of(), Map.of(), currentCustomer);

        assertThatThrownBy(() -> transferMoney.handle(new TransferMoneyCommand(transactionAmount, receiverAccountIban, bic, null))).isInstanceOf(UnknownBeneficiaryException.class)
                .hasMessage("Cannot find any account with the accountIdentifier: " + receiverAccountIban + " in your beneficiaries list.");
    }

    @Test
    void shouldThrowAnExceptionWhenThereIsNoCurrentCustomer() {
        String receiverAccountIban = "FR5030004000700000157389538";
        String bic = "AGRIFRPP989";

        TransferMoney transferMoney = buildTransferMoney(new HashMap<>(), List.of(), List.of(), List.of(), Map.of(), null);

        assertThatThrownBy(() -> transferMoney.handle(new TransferMoneyCommand(new BigDecimal(100), receiverAccountIban, bic, null)))
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheBicOfTheBankIsInvalid() {
        String receiverAccountIban = "FR5030004000700000157389538";
        String bic = "invalidBic";

        TransferMoney transferMoney = buildTransferMoney(new HashMap<>(), List.of(), List.of(), List.of(), Map.of(), null);
        assertThatThrownBy(() -> transferMoney.handle(new TransferMoneyCommand(new BigDecimal(100), receiverAccountIban, bic, null)))
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

        accountStore.put(receiverAccountIban, new Account.Builder()
                .withId(receiverAccountId)
                .withIban(new Iban(receiverAccountIban))
                .withBalance(new BigDecimal(100))
                .build());

        beneficiaryRepository.insert(senderAccountId, new Beneficiary("AE434", new Iban(receiverAccountIban), new Bic(bic), receiverAccountFirstName + " " + receiverAccountLastName, new Iban(senderAccountIban)));

        TransferMoney transferMoney = buildTransferMoney(accountStore, List.of(senderTransactionId, receiverTransactionId), List.of(), List.of(), Map.of(), currentCustomer);

        assertThatThrownBy(() -> transferMoney.handle(new TransferMoneyCommand(transactionAmount, receiverAccountIban, bic, null)))
                .isInstanceOf(InsufficientBalanceException.class);
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

        beneficiaryRepository.insert(senderAccountId, new Beneficiary("AE434", new Iban(receiverAccountIban), new Bic(bic), receiverAccountFirstName + " " + receiverAccountLastName, new Iban(senderAccountIban)));


        TransferMoney transferMoney = buildTransferMoney(accountStore, List.of(senderTransactionId, receiverTransactionId), List.of(), List.of(), new HashMap<>(), currentCustomer);

        assertThatThrownBy(() -> transferMoney.handle(new TransferMoneyCommand(transactionAmount, receiverAccountIban, bic, null)))
                .isInstanceOf(UnknownAccountWithIbanException.class)
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
        TransferMoney transferMoney = buildTransferMoney(new HashMap<>(), List.of(senderTransactionId, receiverTransactionId), List.of(), List.of(), new HashMap<>(), currentCustomer);
        assertThatThrownBy(() -> transferMoney.handle(new TransferMoneyCommand(transactionAmount, receiverAccountIban, bic, null)))
                .isInstanceOf(UnknownAccountWithCustomerId.class)
                .hasMessage("There is no account with the customerId: " + customerId);

    }


    private TransferMoney buildTransferMoney(Map<String, Account> accountStore, List<String> ids, List<Transaction> capturedTransactions, List<String> capturedAccountInfos, Map<String, Transaction> transactionStore, Customer customer) {
        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(customer);
        TransactionRepository transactionRepository = new InMemoryTransactionRepository(transactionStore);
        IdGenerator idGenerator = new InMemoryIdGenerator(ids);
        ExternalBankTransactionsGateway externalBankTransactionsGateway = new InMemoryExternalBankTransactionsGateway(capturedTransactions, capturedAccountInfos);
        AccountRepository accountRepository = new InMemoryAccountRepository(accountStore);
        return new TransferMoney(accountRepository, beneficiaryRepository, transactionRepository, dateProvider, idGenerator, externalBankTransactionsGateway, authenticationGateway);

    }
}