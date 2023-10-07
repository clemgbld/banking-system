package app.netlify.clementgombauld.banking.core.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.core.domain.Customer;

import java.util.Optional;

public class InMemoryAuthenticationGateway implements AuthenticationGateway {

    private Customer customer;

    @Override
    public void authenticate(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Optional<Customer> currentCustomer() {
        return Optional.ofNullable(customer);
    }
}
