package app.netlify.clementgombauld.banking.account_bc.core.domain;

import app.netlify.clementgombauld.banking.account_bc.core.domain.exceptions.InvalidIbanException;

import java.util.Objects;

public record Iban(String value) {

    public Iban(String value) {
      this.value = validateIban(value);
    }

    private String validateIban(String value){
        try {
            var iban = org.iban4j.Iban.valueOf(value);
            return iban.toString();
        }catch (Exception e){
            throw new InvalidIbanException(value);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Iban iban)) return false;
        return value.equals(iban.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
