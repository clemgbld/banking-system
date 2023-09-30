package app.netlify.clementgombauld.banking.core.domain;

import java.util.Objects;

public record Currency(String value) {
    public boolean isBankCurrency() {
        return value.equals(BankInfoType.CURRENCY.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency currency)) return false;
        return Objects.equals(value, currency.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "value='" + value + '\'' +
                '}';
    }
}
