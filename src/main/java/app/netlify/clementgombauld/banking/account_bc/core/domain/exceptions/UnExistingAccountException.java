package app.netlify.clementgombauld.banking.account_bc.core.domain.exceptions;

public class UnExistingAccountException extends RuntimeException{

    public UnExistingAccountException(String id) {
        super(String.format("Customer with id: %s does not have any account",id));
    }
}
