package app.netlify.clementgombauld.banking.identityaccess.domain.exceptions;

public class PasswordNumberRequiredException extends IllegalArgumentException {
    public PasswordNumberRequiredException() {
        super("Password must at least have one number.");
    }
}
