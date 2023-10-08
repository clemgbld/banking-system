package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class NoIbanException extends RuntimeException {
    public NoIbanException() {
        super("No IBAN provided.");
    }
}
