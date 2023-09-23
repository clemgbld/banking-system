package app.netlify.clementgombauld.banking.core.domain;

public class Customer {
    private final String id;
    private final String firstName;

    private final String lastName;

    private  Account account;

    public Customer(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
