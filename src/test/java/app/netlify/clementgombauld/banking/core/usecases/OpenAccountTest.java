package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryCustomerRepository;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryIbanGenerator;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryIdGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class OpenAccountTest {
    @Test
    void shouldOpenANewAccountWithABalanceOfZeroInitially() {
        String customerId = "134343";
        String firstName = "Jean";
        String lastName = "Paul";
        String accountId = "1";
        String generatedIban = "FR1420041010050500013M02606";

        Customer customer = new Customer(customerId, firstName, lastName);


        Map<String, Customer> customerStore = new HashMap<>();

        CustomerRepository customerRepository = new InMemoryCustomerRepository(customerStore);

        IbanGenerator ibanGenerator = new InMemoryIbanGenerator(generatedIban);

        IdGenerator idGenerator = new InMemoryIdGenerator(List.of(accountId));

        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway();

        authenticationGateway.authenticate(customer);

        OpenAccount openAccount = new OpenAccount(customerRepository, ibanGenerator, idGenerator, authenticationGateway);

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

}