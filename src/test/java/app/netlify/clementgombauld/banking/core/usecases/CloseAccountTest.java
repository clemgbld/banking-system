package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryCustomerRepository;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryExternalBankTransactionsGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CloseAccountTest {

    private AuthenticationGateway authenticationGateway;

    @BeforeEach
    void setUp() {
        authenticationGateway = new InMemoryAuthenticationGateway();
    }


    @Test
    void shouldDeleteTheCurrentCustomerWhenHeWantsToCloseHisZeroBalanceAccount() {
        String customerId = "1234";
        String firstName = "James";
        String lastName = "Bond";
        String accountId = "1";
        String accountIban = "FR5030004000700000157389538";
        String externalIban = "FR1420041010050500013M02606";
        String bic = "AGRIFRPP989";
        String externalBic = "BNPAFRPP123";

        Customer customer = new Customer(customerId, firstName, lastName);
        customer.openAccount(new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .build());
        authenticationGateway.authenticate(customer);
        Map<String, Customer> customerStore = new HashMap<>();
        customerStore.put(customerId, customer);

        CloseAccount closeAccount = buildCloseAccount(customerStore, List.of(), List.of());

        closeAccount.handle(externalIban, externalBic, bic);

        assertThat(customerStore.get(customerId)).isNull();
    }

    CloseAccount buildCloseAccount(Map<String, Customer> customerStore, List<Transaction> transactions, List<String> externalBankInfos) {
        CustomerRepository customerRepository = new InMemoryCustomerRepository(customerStore);
        ExternalBankTransactionsGateway externalBankTransactionsGateway = new InMemoryExternalBankTransactionsGateway(transactions, externalBankInfos);
        return new CloseAccount(customerRepository, authenticationGateway, externalBankTransactionsGateway);
    }


}