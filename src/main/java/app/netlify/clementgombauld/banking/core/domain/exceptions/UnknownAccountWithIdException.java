package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class UnknownAccountWithIdException extends RuntimeException{
    public UnknownAccountWithIdException(String id) {
        super(String.format("There is no account with the id: %s",id));
    }
}
