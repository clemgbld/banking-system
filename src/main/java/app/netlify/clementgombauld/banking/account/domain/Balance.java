package app.netlify.clementgombauld.banking.account.domain;

import app.netlify.clementgombauld.banking.account.domain.exceptions.InsufficientBalanceException;

import java.math.BigDecimal;
import java.util.Objects;

public record Balance(BigDecimal value) {

    public static final int COMPARATOR = 0;
    public static final int ZERO_BALANCE = 0;

    public Balance(BigDecimal value) {
        this.value = checkBalanceSufficiency(value);
    }

    public Balance add(BigDecimal amount) {
        return new Balance(value.add(amount));
    }

    public Balance subtract(BigDecimal amount) {

        return new Balance(value.subtract(amount));
    }


    public boolean isEmpty() {
        return this.value.compareTo(new BigDecimal(0)) == COMPARATOR;
    }

    public BigDecimal negate() {
        return value.negate();
    }

    private boolean isBalanceInsufficient(BigDecimal amount) {
        return amount.compareTo(new BigDecimal(ZERO_BALANCE)) < COMPARATOR;
    }

    private BigDecimal checkBalanceSufficiency(BigDecimal amount) {
        if (isBalanceInsufficient(amount)) {
            throw new InsufficientBalanceException();
        }
        return amount;
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
