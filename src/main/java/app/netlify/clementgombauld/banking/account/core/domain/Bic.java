package app.netlify.clementgombauld.banking.account.core.domain;

import app.netlify.clementgombauld.banking.account.core.domain.exceptions.InvalidBicException;

import java.util.Objects;

public record Bic(String value) {
    public Bic(String value) {
        this.value = validateBic(value);
    }

    private String validateBic(String value){
        try {
            var bic = org.iban4j.Bic.valueOf(value);
            return bic.toString();
        }catch (Exception exception){
            throw new InvalidBicException(value);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bic bic)) return false;
        return value.equals(bic.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
