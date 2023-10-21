package app.netlify.clementgombauld.banking.identityaccess.domain.exceptions;

public class PasswordSpecialCharacterRequiredException extends IllegalArgumentException {
    public PasswordSpecialCharacterRequiredException() {
        super("Password must at least have one special character.");
    }
}
