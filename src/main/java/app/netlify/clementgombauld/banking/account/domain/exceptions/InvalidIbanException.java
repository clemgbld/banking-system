package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class InvalidIbanException extends RuntimeException {
    public InvalidIbanException(String iban) {
        super(String.format("accountIdentifier: %s is invalid.", iban));
    }
}
