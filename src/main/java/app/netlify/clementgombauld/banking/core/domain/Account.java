package app.netlify.clementgombauld.banking.core.domain;


import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;


public class Account {
    private final String id;

    private final String iban;

    private Balance balance;


    private Account(Builder builder) {
        this.id = builder.id;
        this.iban = builder.iban;
        this.balance = initBalance(builder.balance);
    }


    public static class Builder {
        private String id;
        private String iban;
        private BigDecimal balance;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withIban(String iban) {
            this.iban = iban;
            return this;
        }


        public Builder withBalance(BigDecimal money) {
            this.balance = money;
            return this;
        }


        public Account build() {
            return new Account(this);
        }


    }


    public void deposit(BigDecimal transactionAmount) {
        balance = balance.add(transactionAmount);
    }

    public void withdraw(BigDecimal transactionAmount) {
        balance.checkBalanceSufficiency(transactionAmount);
        deposit(transactionAmount.negate());
    }


    public String getId() {
        return id;
    }


    public String getIban() {
        return iban;
    }

    public BigDecimal getBalance() {
        return balance.value();
    }


    private Balance initBalance(BigDecimal balance) {
        BigDecimal initialBalance = Optional.ofNullable(balance).orElse(new BigDecimal(0));
        return new Balance(initialBalance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(id, account.id) && Objects.equals(iban, account.iban) && Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, iban, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", iban='" + iban + '\'' +
                ", balance=" + balance +
                '}';
    }
}
