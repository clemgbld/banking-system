package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class NoBicException extends RuntimeException {
    public NoBicException() {
        super("No BIC provided.");
    }
}
