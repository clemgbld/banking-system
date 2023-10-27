package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class SameBankException extends IllegalArgumentException {
    public SameBankException() {
        super("Your account does not belong to an external bank.");
    }
}
