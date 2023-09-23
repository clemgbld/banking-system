package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.UnExistingAccountException;

import java.util.Optional;

public class Customer {
    private final String id;
    private final String firstName;

    private final String lastName;

    private Account account;

    public Customer(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
       return Optional.ofNullable(account).orElseThrow(()->{
           throw new UnExistingAccountException(id);
       });
    }
}
