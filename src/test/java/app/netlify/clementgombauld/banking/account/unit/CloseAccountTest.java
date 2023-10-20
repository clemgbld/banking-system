package app.netlify.clementgombauld.banking.account.unit;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.domain.exceptions.*;
import app.netlify.clementgombauld.banking.account.unit.inMemory.*;
import app.netlify.clementgombauld.banking.account.usecases.CloseAccount;
import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import app.netlify.clementgombauld.banking.common.domain.IdGenerator;
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

class CloseAccountTest {

    public static final String ACCOUNT_ID = "1";

    public static final String TRANSACTION_ID1 = "1A324524";

    public static final String TRANSACTION_ID2 = "FQE45A345";

    public static final String ACCOUNT_NAME = "John Doe";


    private AccountRepository accountRepository;

    private Map<String, Account> accountStore;

    public static final long CURRENT_DATE_IN_MS = 2534543253252L;
    public static final Instant CURRENT_DATE = Instant.ofEpochMilli(CURRENT_DATE_IN_MS);

    private DateProvider dateProvider;

    private List<Transaction> transactions;

    private List<String> bankInfos;

    private ExternalBankTransactionsGateway externalBankTransactionsGateway;

    private IdGenerator idGenerator;

    private Map<String, Transaction> transactionStore;

    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        accountStore = new HashMap<>();
        accountRepository = new InMemoryAccountRepository(accountStore);
        dateProvider = new InMemoryDateProvider(CURRENT_DATE_IN_MS);
        transactions = new ArrayList<>();
        bankInfos = new ArrayList<>();
        externalBankTransactionsGateway = new InMemoryExternalBankTransactionsGateway(transactions, bankInfos);
        idGenerator = new InMemoryIdGenerator(List.of(TRANSACTION_ID1, TRANSACTION_ID2));
        transactionStore = new HashMap<>();
        transactionRepository = new InMemoryTransactionRepository(transactionStore);
    }


    @Test
    void shouldDeleteTheAccountWithoutAnyAdditionalMoneyTransferWhenTheAccountHasABalanceOfZero() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountIban = "FR1420041010050500013M02606";

        Customer customer = new Customer(customerId, firstName, lastName);

        Account accountToDelete = new Account.Builder()
                .withId(ACCOUNT_ID)
                .withIban(new Iban(accountIban))
                .build();

        accountStore.put(customerId, accountToDelete);
        accountStore.put(ACCOUNT_ID, accountToDelete);

        CloseAccount closeAccount = buildCloseAccount(customer);

        closeAccount.handle(null, null, null, null);

        assertThat(accountStore.get(ACCOUNT_ID)).isNull();

    }

    @Test
    void shouldUpdateAndDeleteTheAccountAsWellToTransferTheRemainingMoneyToAnotherBankWhenTheBalanceIsNotEmpty() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountIban = "FR1420041010050500013M02606";
        String externalAccountIban = "FR5030004000700000157389538";
        String bic = "AGRIFRPP989";
        String externalBic = "BNPAFRPP123";

        Customer customer = new Customer(customerId, firstName, lastName);

        Account accountToDelete = new Account.Builder()
                .withId(ACCOUNT_ID)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(10))
                .build();

        accountStore.put(customerId, accountToDelete);
        accountStore.put(ACCOUNT_ID, accountToDelete);
        accountStore.put(accountIban, accountToDelete);

        CloseAccount closeAccount = buildCloseAccount(customer);

        closeAccount.handle(externalAccountIban, externalBic, bic, ACCOUNT_NAME);

        assertThat(accountStore.get(accountIban)).isEqualTo(
                new Account.Builder()
                        .withId(ACCOUNT_ID)
                        .withIban(new Iban(accountIban))
                        .withBalance(new BigDecimal(0))
                        .build()
        );

        assertThat(accountStore.get(ACCOUNT_ID)).isNull();

        assertThat(bankInfos).isEqualTo(List.of(externalAccountIban, externalBic));
        assertThat(transactions).isEqualTo(List.of(new Transaction(TRANSACTION_ID1, CURRENT_DATE, new BigDecimal(10), accountIban, bic, firstName + " " + lastName)));
        assertThat(transactionStore.get(ACCOUNT_ID)).isEqualTo(new Transaction(TRANSACTION_ID2, CURRENT_DATE, new BigDecimal(-10), externalAccountIban, externalBic, ACCOUNT_NAME));

    }

    @Test
    void shouldThrowAnExceptionWhenThereIsNoCurrentCustomer() {
        CloseAccount closeAccount = buildCloseAccount(null);
        assertThatThrownBy(() -> closeAccount.handle(null, null, null, ACCOUNT_NAME))
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheCustomerHasNoAccount() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        Customer customer = new Customer(customerId, firstName, lastName);

        CloseAccount closeAccount = buildCloseAccount(customer);
        assertThatThrownBy(() -> closeAccount.handle(null, null, null, ACCOUNT_NAME))
                .isInstanceOf(UnknownAccountWithCustomerId.class)
                .hasMessage("There is no account with the customerId: " + customerId);
    }

    @Test
    void shouldThrowANoIbanExceptionWhenTheBalanceIsNotEmptyAndWhenThereIsNoExternalBankIban() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountIban = "FR1420041010050500013M02606";
        String externalBic = "BNPAFRPP123";
        String bic = "AGRIFRPP989";

        Customer customer = new Customer(customerId, firstName, lastName);

        accountStore.put(customerId, new Account.Builder()
                .withId(ACCOUNT_ID)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(10))
                .build());

        CloseAccount closeAccount = buildCloseAccount(customer);

        assertThatThrownBy(() -> closeAccount.handle(null, externalBic, bic, ACCOUNT_NAME))
                .isInstanceOf(NoIbanException.class)
                .hasMessage("No IBAN provided.");

    }

    @Test
    void shouldThrowAnExceptionWhenTheBalanceIsNotEmptyAndWhenTheExternalBankIbanIsNotValid() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountIban = "FR1420041010050500013M02606";
        String externalAccountIban = "invalidIban";
        String externalBic = "BNPAFRPP123";
        String bic = "AGRIFRPP989";

        Customer customer = new Customer(customerId, firstName, lastName);

        accountStore.put(customerId, new Account.Builder()
                .withId(ACCOUNT_ID)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(10))
                .build());


        CloseAccount closeAccount = buildCloseAccount(customer);

        assertThatThrownBy(() -> closeAccount.handle(externalAccountIban, externalBic, bic, ACCOUNT_NAME))
                .isInstanceOf(InvalidIbanException.class)
                .hasMessage("accountIdentifier: " + externalAccountIban + " is invalid.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheBalanceIsNotEmptyAndWhenThereIsNoExternalBankBic() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountIban = "FR1420041010050500013M02606";
        String externalAccountIban = "FR5030004000700000157389538";
        String bic = "AGRIFRPP989";

        Customer customer = new Customer(customerId, firstName, lastName);

        accountStore.put(customerId, new Account.Builder()
                .withId(ACCOUNT_ID)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(10))
                .build());


        CloseAccount closeAccount = buildCloseAccount(customer);

        assertThatThrownBy(() -> closeAccount.handle(externalAccountIban, null, bic, ACCOUNT_NAME))
                .isInstanceOf(NoBicException.class)
                .hasMessage("No BIC provided.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheBalanceIsNotEmptyAndWhenTheExternalBicIsInvalid() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountIban = "FR1420041010050500013M02606";
        String externalAccountIban = "FR5030004000700000157389538";
        String externalBic = "InvalidBic";
        String bic = "AGRIFRPP989";

        Customer customer = new Customer(customerId, firstName, lastName);

        accountStore.put(customerId, new Account.Builder()
                .withId(ACCOUNT_ID)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(10))
                .build());

        CloseAccount closeAccount = buildCloseAccount(customer);

        assertThatThrownBy(() -> closeAccount.handle(externalAccountIban, externalBic, bic, ACCOUNT_NAME))
                .isInstanceOf(InvalidBicException.class)
                .hasMessage("bic: " + externalBic + " is invalid.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheBalanceIsNotEmptyAndWhenTheBankBicIsInvalid() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountIban = "FR1420041010050500013M02606";
        String externalAccountIban = "FR5030004000700000157389538";
        String externalBic = "AGRIFRPP989";
        String bic = "invalidBankBic";

        Customer customer = new Customer(customerId, firstName, lastName);

        accountStore.put(customerId, new Account.Builder()
                .withId(ACCOUNT_ID)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(10))
                .build());


        CloseAccount closeAccount = buildCloseAccount(customer);

        assertThatThrownBy(() -> closeAccount.handle(externalAccountIban, externalBic, bic, ACCOUNT_NAME))
                .isInstanceOf(InvalidBicException.class)
                .hasMessage("bic: " + bic + " is invalid.");
    }

    private CloseAccount buildCloseAccount(Customer customer) {
        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(customer);
        return new CloseAccount(accountRepository, authenticationGateway, externalBankTransactionsGateway, dateProvider, idGenerator, transactionRepository);
    }


}