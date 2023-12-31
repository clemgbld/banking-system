package app.netlify.clementgombauld.banking.account.domain;

import app.netlify.clementgombauld.banking.account.domain.exceptions.InvalidIbanException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.NoIbanException;

import java.util.Objects;
import java.util.Optional;


public class Iban {
    private final org.iban4j.Iban iban;

    public Iban(String value) {
        this.iban = validateIban(value);
    }

    public String value() {
        return iban.toString();
    }

    private org.iban4j.Iban validateIban(String value) {
        String nonNullValue = Optional.ofNullable(value)
                .orElseThrow(NoIbanException::new);
        try {
            return org.iban4j.Iban.valueOf(nonNullValue);
        } catch (Exception e) {
            throw new InvalidIbanException(nonNullValue);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Iban iban1)) return false;
        return Objects.equals(iban, iban1.iban);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iban);
    }

    @Override
    public String toString() {
        return "Iban{" +
                "iban=" + iban +
                '}';
    }
}
