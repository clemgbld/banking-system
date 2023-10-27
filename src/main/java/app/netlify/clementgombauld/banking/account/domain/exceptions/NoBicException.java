package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class NoBicException extends IllegalArgumentException {
    public NoBicException() {
        super("No BIC provided.");
    }
}
