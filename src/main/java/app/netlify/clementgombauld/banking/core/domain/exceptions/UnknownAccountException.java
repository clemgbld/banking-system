package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class UnknownAccountException extends RuntimeException{
    public UnknownAccountException( String iban) {
        super(String.format("There is no account with the iban: %s",iban));
    }
}
