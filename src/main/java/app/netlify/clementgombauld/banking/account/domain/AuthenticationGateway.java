package app.netlify.clementgombauld.banking.account.domain;

import java.util.Optional;

public interface AuthenticationGateway {
    Optional<Customer> currentCustomer();
}
