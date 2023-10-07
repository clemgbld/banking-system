package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.AccountAlreadyOpenedException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.core.infra.inMemory.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class OpenAccountTest {

    private AuthenticationGateway authenticationGateway;

    @BeforeEach
    void setUp() {
        authenticationGateway = new InMemoryAuthenticationGateway();
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

        authenticationGateway.authenticate(customer);

        OpenAccount openAccount = buildOpenAccount(accountStore, generatedIban, List.of(accountId));

        openAccount.handle();

        Account createdAccount = accountStore.get(generatedIban);

        assertThat(createdAccount).isEqualTo(
                new Account.Builder().withId(accountId)
                        .withIban(new Iban(generatedIban))
                        .withCustomer(customer)
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

        authenticationGateway.authenticate(customer);

        OpenAccount openAccount = buildOpenAccount(accountStore, generatedIban, List.of(accountId));

        assertThatThrownBy(openAccount::handle)
                .isInstanceOf(AccountAlreadyOpenedException.class)
                .hasMessage("Account with customerId: " + customerId + " is already opened.");

    }

    @Test
    void shouldThrowAnExceptionWhenThereIsNoAuthenticatedCustomer() {
        String accountId = "1";
        String generatedIban = "FR1420041010050500013M02606";
        OpenAccount openAccount = buildOpenAccount(new HashMap<>(), generatedIban, List.of(accountId));
        assertThatThrownBy(openAccount::handle)
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");

    }

    private OpenAccount buildOpenAccount(Map<String, Account> accountStore, String generatedIban, List<String> ids) {

        IbanGenerator ibanGenerator = new InMemoryIbanGenerator(generatedIban);
        AccountRepository accountRepository = new InMemoryAccountRepository(accountStore);
        IdGenerator idGenerator = new InMemoryIdGenerator(ids);
        return new OpenAccount(accountRepository, ibanGenerator, idGenerator, authenticationGateway);
    }

}