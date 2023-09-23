package app.netlify.clementgombauld.banking.account.core.domain.exceptions;

public class InvalidIbanException extends RuntimeException{
    public InvalidIbanException(String iban) {
        super(String.format("iban: %s is invalid.",iban));
    }
}
