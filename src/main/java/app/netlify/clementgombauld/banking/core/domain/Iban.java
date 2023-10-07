package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.InvalidIbanException;

import java.util.Objects;


public class Iban {
    private final org.iban4j.Iban iban;

    public Iban(String value) {
        this.iban = validateIban(value);
    }

    public String value() {
        return iban.toString();
    }

    private org.iban4j.Iban validateIban(String value) {
        try {
            return org.iban4j.Iban.valueOf(value);
        } catch (Exception e) {
            throw new InvalidIbanException(value);
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
