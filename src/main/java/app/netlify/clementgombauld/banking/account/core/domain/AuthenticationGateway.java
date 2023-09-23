package app.netlify.clementgombauld.banking.account.core.domain;

import java.util.Optional;

public interface AuthenticationGateway {
    void authenticate(Customer customer);

    Optional<Customer> currentCustomer();
}
