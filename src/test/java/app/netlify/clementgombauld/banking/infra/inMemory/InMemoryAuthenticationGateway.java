package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.account_bc.core.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account_bc.core.domain.Customer;

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
