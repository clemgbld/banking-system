package app.netlify.clementgombauld.banking.core.domain;

import java.math.BigDecimal;

public class Account {

   private final String id;

    private final String iban;

    private final String bic;

    private BigDecimal balance;

    public Account(String id,String iban, String bic, BigDecimal balance) {
        this.id = id;
        this.iban = iban;
        this.bic = bic;
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void withdraw(BigDecimal transactionAmount) {
        this.balance = balance.subtract(transactionAmount);
    }

    public String getIban() {
        return iban;
    }
}
