package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class InvalidIbanException extends RuntimeException{
    public InvalidIbanException(String iban) {
        super(String.format("iban: %s is invalid.",iban));
    }
}
