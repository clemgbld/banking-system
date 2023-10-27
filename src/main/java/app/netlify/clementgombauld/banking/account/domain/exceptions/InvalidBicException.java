package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class InvalidBicException extends IllegalArgumentException {
    public InvalidBicException(String bic) {
        super(String.format("bic: %s is invalid.", bic));
    }
}
