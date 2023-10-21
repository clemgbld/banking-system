package app.netlify.clementgombauld.banking.identityaccess.domain.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super(String.format("Account with email %s already exists.", email));
    }
}
