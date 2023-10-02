package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class SameBankException extends RuntimeException {
    public SameBankException() {
        super("Your account does not belong to an external bank.");
    }
}
