package app.netlify.clementgombauld.banking.account.core.domain.exceptions;

public class InvalidBicException extends RuntimeException{
    public InvalidBicException(String bic) {
        super(String.format("bic: %s is invalid.",bic));
    }
}