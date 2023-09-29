package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.InsufficientBalanceException;

import java.math.BigDecimal;
import java.util.Objects;

public record Balance(BigDecimal value) {

    public static final int COMPARATOR = 0;
    public static final int ZERO_BALANCE = 0;

    Balance add(BigDecimal amount) {
        return new Balance(value.add(amount));
    }

    void checkBalanceSufficiency(BigDecimal amount) {
        if (isBalanceInsufficient(amount)) {
            throw new InsufficientBalanceException();
        }
    }

    private boolean isBalanceInsufficient(BigDecimal amount) {
        return value.subtract(amount).compareTo(new BigDecimal(ZERO_BALANCE)) < COMPARATOR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Balance balance)) return false;
        return value.equals(balance.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Balance{" +
                "value=" + value +
                '}';
    }
}
