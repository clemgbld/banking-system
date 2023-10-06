package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class AccountAlreadyOpenedException extends RuntimeException {
    public AccountAlreadyOpenedException(String customerId) {
        super(String.format("Customer with id: %s has already opened an account.", customerId));
    }
}
