package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class NoIbanException extends IllegalArgumentException {
    public NoIbanException() {
        super("No IBAN provided.");
    }
}
