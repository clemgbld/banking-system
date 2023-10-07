package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.AccountAlreadyOpenedException;


import java.util.Objects;

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

    public void openAccount(Account account) {
        if (!Objects.isNull(this.account)) {
            throw new AccountAlreadyOpenedException(id);
        }
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public String fullName() {
        return String.format("%s %s", firstName, lastName);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(id, customer.id) && Objects.equals(firstName, customer.firstName) && Objects.equals(lastName, customer.lastName) && Objects.equals(account, customer.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, account);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", account=" + account +
                '}';
    }
}
