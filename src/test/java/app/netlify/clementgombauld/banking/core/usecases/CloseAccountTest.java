package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.infra.inMemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.core.infra.inMemory.InMemoryAuthenticationGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CloseAccountTest {

    private AuthenticationGateway authenticationGateway;

    private AccountRepository accountRepository;

    private Map<String, Account> accountStore;

    @BeforeEach
    void setUp() {
        authenticationGateway = new InMemoryAuthenticationGateway();
        accountStore = new HashMap<>();
        accountRepository = new InMemoryAccountRepository(accountStore);
    }


    @Test
    void shouldDeleteTheAccountWithoutAnyAdditionalMoneyTransferWhenTheAccountHasABalanceOfZero() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountId = "1";
        String accountIban = "FR1420041010050500013M02606";

        Customer customer = new Customer(customerId, firstName, lastName);

        Account accountToDelete = new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .build();

        accountStore.put(customerId, accountToDelete);
        accountStore.put(accountId, accountToDelete);

        authenticationGateway.authenticate(customer);

        CloseAccount closeAccount = new CloseAccount(accountRepository, authenticationGateway);

        closeAccount.handle(null, null, null);

        assertThat(accountStore.get(accountId)).isNull();
    }


}