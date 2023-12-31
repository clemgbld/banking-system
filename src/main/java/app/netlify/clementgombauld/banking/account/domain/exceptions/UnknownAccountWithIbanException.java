package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class UnknownAccountWithIbanException extends IllegalArgumentException {
    public UnknownAccountWithIbanException(String iban) {
        super(String.format("There is no account with the accountIdentifier: %s", iban));
    }
}
