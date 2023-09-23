package app.netlify.clementgombauld.banking.account_bc.core.domain.exceptions;

public class UnknownAccountWithIbanException extends RuntimeException{
    public UnknownAccountWithIbanException(String iban) {
        super(String.format("There is no account with the iban: %s",iban));
    }
}
