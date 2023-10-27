package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class UnknownAccountWithCustomerId extends IllegalArgumentException {

    public UnknownAccountWithCustomerId(String customerId) {
        super(String.format("There is no account with the customerId: %s", customerId));
    }
}
