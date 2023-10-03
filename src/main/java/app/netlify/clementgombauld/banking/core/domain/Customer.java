package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.UnExistingAccountException;

import java.util.Objects;
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

    public void openAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return Optional.ofNullable(account).orElseThrow(() -> {
            throw new UnExistingAccountException(id);
        });
    }

    public String fullName() {
        return String.format("%s %s", firstName, lastName);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(id, customer.id) && Objects.equals(firstName, customer.firstName) && Objects.equals(lastName, customer.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
