package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class SameIbanThanAccountException extends RuntimeException {
    public SameIbanThanAccountException() {
        super("You can't add yourself as a beneficiary.");
    }
}
