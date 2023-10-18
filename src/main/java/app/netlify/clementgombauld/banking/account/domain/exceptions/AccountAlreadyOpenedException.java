package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class AccountAlreadyOpenedException extends RuntimeException {
    public AccountAlreadyOpenedException(String customerId) {
        super(String.format("Account with customerId: %s is already opened.", customerId));
    }
}