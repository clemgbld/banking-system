package app.netlify.clementgombauld.banking.account.domain;

import java.util.Optional;

public interface AuthenticationGateway {
    void authenticate(Customer customer);

    Optional<Customer> currentCustomer();
}
