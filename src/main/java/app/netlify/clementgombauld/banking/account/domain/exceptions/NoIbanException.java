package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class NoIbanException extends RuntimeException {
    public NoIbanException() {
        super("No IBAN provided.");
    }
}
