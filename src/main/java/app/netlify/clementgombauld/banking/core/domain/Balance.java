package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.InsufficientBalanceException;

import java.math.BigDecimal;

public record Balance(BigDecimal value) {

    public static final int COMPARATOR = 0;
    public static final int ZERO_BALANCE = 0;

    Balance add(BigDecimal amount){
        return new Balance(value.add(amount));
    }

    void checkBalanceSufficiency(BigDecimal amount){
        if(isBalanceInsufficient(amount)){
            throw new InsufficientBalanceException();
        }
    }

    private boolean isBalanceInsufficient(BigDecimal amount) {
        return value.subtract(amount).compareTo(new BigDecimal(ZERO_BALANCE)) < COMPARATOR;
    }


}
