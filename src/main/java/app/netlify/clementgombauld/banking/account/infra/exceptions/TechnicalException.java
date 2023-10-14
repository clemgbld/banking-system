package app.netlify.clementgombauld.banking.account.infra.exceptions;

public class TechnicalException extends RuntimeException {
    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
