package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class NoCurrentCustomerException extends RuntimeException{
    public NoCurrentCustomerException() {
        super("No current customer authenticated.");
    }
}
