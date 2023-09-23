package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class InsufficientBalanceException extends RuntimeException{
    public InsufficientBalanceException() {
        super("You cannot perform this operation your balance is insufficient");
    }
}
