package app.netlify.clementgombauld.banking.account.unit.inMemory;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;

import java.util.Optional;

public class InMemoryAuthenticationGateway implements AuthenticationGateway {

    private Customer customer;

    public InMemoryAuthenticationGateway() {
    }

    public InMemoryAuthenticationGateway(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void authenticate(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Optional<Customer> currentCustomer() {
        return Optional.ofNullable(customer);
    }
}
