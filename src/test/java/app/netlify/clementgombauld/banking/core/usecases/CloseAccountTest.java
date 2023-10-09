package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.*;
import app.netlify.clementgombauld.banking.core.infra.inMemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.core.infra.inMemory.InMemoryAuthenticationGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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

    @Test
    void shouldThrowAnExceptionWhenThereIsNoCurrentCustomer() {
        CloseAccount closeAccount = new CloseAccount(accountRepository, authenticationGateway);
        assertThatThrownBy(() -> closeAccount.handle(null, null, null))
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheCustomerHasNoAccount() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        Customer customer = new Customer(customerId, firstName, lastName);
        authenticationGateway.authenticate(customer);

        CloseAccount closeAccount = new CloseAccount(accountRepository, authenticationGateway);
        assertThatThrownBy(() -> closeAccount.handle(null, null, null))
                .isInstanceOf(UnknownAccountWithCustomerId.class)
                .hasMessage("There is no account with the customerId: " + customerId);
    }

    @Test
    void shouldThrowANoIbanExceptionWhenTheBalanceIsNotEmptyAndWhenThereIsNoExternalBankIban() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountId = "1";
        String accountIban = "FR1420041010050500013M02606";
        String externalBic = "BNPAFRPP123";
        String bic = "AGRIFRPP989";

        Customer customer = new Customer(customerId, firstName, lastName);

        accountStore.put(customerId, new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(10))
                .build());

        authenticationGateway.authenticate(customer);

        CloseAccount closeAccount = new CloseAccount(accountRepository, authenticationGateway);

        assertThatThrownBy(() -> closeAccount.handle(null, externalBic, bic))
                .isInstanceOf(NoIbanException.class)
                .hasMessage("No IBAN provided.");

    }

    @Test
    void shouldThrowAnExceptionWhenTheBalanceIsNotEmptyAndWhenTheExternalBankIbanIsNotValid() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountId = "1";
        String accountIban = "FR1420041010050500013M02606";
        String externalAccountIban = "invalidIban";
        String externalBic = "BNPAFRPP123";
        String bic = "AGRIFRPP989";

        Customer customer = new Customer(customerId, firstName, lastName);

        accountStore.put(customerId, new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(10))
                .build());

        authenticationGateway.authenticate(customer);

        CloseAccount closeAccount = new CloseAccount(accountRepository, authenticationGateway);

        assertThatThrownBy(() -> closeAccount.handle(externalAccountIban, externalBic, bic))
                .isInstanceOf(InvalidIbanException.class)
                .hasMessage("accountIdentifier: " + externalAccountIban + " is invalid.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheBalanceIsNotEmptyAndWhenThereIsNoExternalBankBic() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountId = "1";
        String accountIban = "FR1420041010050500013M02606";
        String externalAccountIban = "FR5030004000700000157389538";
        String bic = "AGRIFRPP989";

        Customer customer = new Customer(customerId, firstName, lastName);

        accountStore.put(customerId, new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(10))
                .build());

        authenticationGateway.authenticate(customer);

        CloseAccount closeAccount = new CloseAccount(accountRepository, authenticationGateway);

        assertThatThrownBy(() -> closeAccount.handle(externalAccountIban, null, bic))
                .isInstanceOf(NoBicException.class)
                .hasMessage("No BIC provided.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheBalanceIsNotEmptyAndWhenTheExternalBicIsInvalid() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountId = "1";
        String accountIban = "FR1420041010050500013M02606";
        String externalAccountIban = "FR5030004000700000157389538";
        String externalBic = "InvalidBic";
        String bic = "AGRIFRPP989";

        Customer customer = new Customer(customerId, firstName, lastName);

        accountStore.put(customerId, new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(10))
                .build());

        authenticationGateway.authenticate(customer);

        CloseAccount closeAccount = new CloseAccount(accountRepository, authenticationGateway);

        assertThatThrownBy(() -> closeAccount.handle(externalAccountIban, externalBic, bic))
                .isInstanceOf(InvalidBicException.class)
                .hasMessage("bic: " + externalBic + " is invalid.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheBalanceIsNotEmptyAndWhenTheBankBicIsInvalid() {
        String customerId = "13434";
        String firstName = "John";
        String lastName = "Smith";
        String accountId = "1";
        String accountIban = "FR1420041010050500013M02606";
        String externalAccountIban = "FR5030004000700000157389538";
        String externalBic = "AGRIFRPP989";
        String bic = "invalidBankBic";

        Customer customer = new Customer(customerId, firstName, lastName);

        accountStore.put(customerId, new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(10))
                .build());

        authenticationGateway.authenticate(customer);

        CloseAccount closeAccount = new CloseAccount(accountRepository, authenticationGateway);

        assertThatThrownBy(() -> closeAccount.handle(externalAccountIban, externalBic, bic))
                .isInstanceOf(InvalidBicException.class)
                .hasMessage("bic: " + bic + " is invalid.");
    }


}