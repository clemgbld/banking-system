package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class NoBicException extends RuntimeException {
    public NoBicException() {
        super("No BIC provided.");
    }
}
