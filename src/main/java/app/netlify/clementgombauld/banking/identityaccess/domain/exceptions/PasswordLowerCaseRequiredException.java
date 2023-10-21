package app.netlify.clementgombauld.banking.identityaccess.domain.exceptions;

public class PasswordLowerCaseRequiredException extends IllegalArgumentException {
    public PasswordLowerCaseRequiredException() {
        super("Password must at least have one lower case letter.");
    }
}
