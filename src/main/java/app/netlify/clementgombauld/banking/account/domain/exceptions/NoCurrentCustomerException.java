package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class NoCurrentCustomerException extends IllegalArgumentException {
    public NoCurrentCustomerException() {
        super("No current customer authenticated.");
    }
}
