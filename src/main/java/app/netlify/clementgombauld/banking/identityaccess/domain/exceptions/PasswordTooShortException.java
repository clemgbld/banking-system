package app.netlify.clementgombauld.banking.identityaccess.domain.exceptions;

public class PasswordTooShortException extends RuntimeException {
    public PasswordTooShortException() {
        super("Password must be at least 8 characters long");
    }
}
