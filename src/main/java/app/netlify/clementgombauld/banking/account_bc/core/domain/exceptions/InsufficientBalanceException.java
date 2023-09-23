package app.netlify.clementgombauld.banking.account_bc.core.domain.exceptions;

public class InsufficientBalanceException extends RuntimeException{
    public InsufficientBalanceException() {
        super("You cannot perform this operation your balance is insufficient");
    }
}
