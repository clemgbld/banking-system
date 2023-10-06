package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryCustomerRepository;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryIbanGenerator;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


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

        Map<String, Customer> customerStore = new HashMap<>();

        authenticationGateway.authenticate(customer);

        OpenAccount openAccount = buildOpenAccount(customerStore, generatedIban, List.of(accountId));

        openAccount.handle();

        Customer customerFromRepository = customerStore.get(customerId);

        Customer exepectCustomer = new Customer(
                customerId,
                firstName,
                lastName
        );

        exepectCustomer.openAccount(
                new Account.Builder()
                        .withId(accountId)
                        .withIban(new Iban(generatedIban))
                        .build()
        );

        assertThat(customerFromRepository).isEqualTo(exepectCustomer);

    }

    private OpenAccount buildOpenAccount(Map<String, Customer> customerStore, String generatedIban, List<String> ids) {
        CustomerRepository customerRepository = new InMemoryCustomerRepository(customerStore);

        IbanGenerator ibanGenerator = new InMemoryIbanGenerator(generatedIban);

        IdGenerator idGenerator = new InMemoryIdGenerator(ids);
        return new OpenAccount(customerRepository, ibanGenerator, idGenerator, authenticationGateway);
        ;
    }

}