package app.netlify.clementgombauld.banking.account.unit;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.domain.exceptions.AccountAlreadyOpenedException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.account.unit.inMemory.*;
import app.netlify.clementgombauld.banking.account.usecases.OpenAccount;
import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import app.netlify.clementgombauld.banking.common.domain.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class OpenAccountTest {

    public static final long CURRENT_DATE_IN_MS = 2534543253252L;
    public static final Instant CURRENT_DATE = Instant.ofEpochMilli(2534543253252L);


    private DateProvider dateProvider;

    @BeforeEach
    void setUp() {
        dateProvider = new InMemoryDateProvider(CURRENT_DATE_IN_MS);
    }

    @Test
    void shouldOpenANewAccountWithABalanceOfZeroInitially() {
        String customerId = "134343";
        String firstName = "Jean";
        String lastName = "Paul";
        String accountId = "1";
        String generatedIban = "FR1420041010050500013M02606";

        Customer customer = new Customer(customerId, firstName, lastName);

        Map<String, Account> accountStore = new HashMap<>();


        OpenAccount openAccount = buildOpenAccount(accountStore, generatedIban, List.of(accountId), customer);

        openAccount.handle();

        Account createdAccount = accountStore.get(generatedIban);

        assertThat(createdAccount).isEqualTo(
                new Account.Builder().withId(accountId)
                        .withIban(new Iban(generatedIban))
                        .withCustomer(customer)
                        .withCreatedOn(CURRENT_DATE)
                        .build()
        );

    }

    @Test
    void shouldThrowAnExceptionWhenTheCustomerHasAlreadyOpenedAnAccount() {
        String customerId = "134343";
        String firstName = "Jean";
        String lastName = "Paul";
        String accountId = "1";
        String generatedIban = "FR1420041010050500013M02606";

        Map<String, Account> accountStore = new HashMap<>();

        Customer customer = new Customer(customerId, firstName, lastName);

        accountStore.put(customerId, new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(generatedIban))
                .build());


        OpenAccount openAccount = buildOpenAccount(accountStore, generatedIban, List.of(accountId), customer);

        assertThatThrownBy(openAccount::handle)
                .isInstanceOf(AccountAlreadyOpenedException.class)
                .hasMessage("Account with customerId: " + customerId + " is already opened.");

    }

    @Test
    void shouldThrowAnExceptionWhenThereIsNoAuthenticatedCustomer() {
        String accountId = "1";
        String generatedIban = "FR1420041010050500013M02606";
        OpenAccount openAccount = buildOpenAccount(new HashMap<>(), generatedIban, List.of(accountId), null);
        assertThatThrownBy(openAccount::handle)
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");

    }

    private OpenAccount buildOpenAccount(Map<String, Account> accountStore, String generatedIban, List<String> ids, Customer customer) {
        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(customer);
        IbanGenerator ibanGenerator = new InMemoryIbanGenerator(generatedIban);
        AccountRepository accountRepository = new InMemoryAccountRepository(accountStore);
        IdGenerator idGenerator = new InMemoryIdGenerator(ids);
        return new OpenAccount(accountRepository, ibanGenerator, idGenerator, authenticationGateway, dateProvider);
    }

}