package app.netlify.clementgombauld.banking.identityaccess.domain.exceptions;

public class PasswordUpperCaseRequiredException extends IllegalArgumentException {
    public PasswordUpperCaseRequiredException() {
        super("Password must at least have one upper case letter.");
    }
}
