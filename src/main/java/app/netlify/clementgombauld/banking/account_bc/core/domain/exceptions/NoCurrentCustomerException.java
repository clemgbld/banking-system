package app.netlify.clementgombauld.banking.account_bc.core.domain.exceptions;

public class NoCurrentCustomerException extends RuntimeException{
    public NoCurrentCustomerException() {
        super("No current customer authenticated.");
    }
}
