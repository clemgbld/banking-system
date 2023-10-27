package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class InsufficientBalanceException extends IllegalArgumentException {
    public InsufficientBalanceException() {
        super("You cannot perform this operation your balance is insufficient");
    }
}
