package app.netlify.clementgombauld.banking.account.domain;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;


public class Account {
    private final String id;

    private final String customerId;

    private final Iban iban;

    private Balance balance;

    private final Instant createdOn;


    private Account(Builder builder) {
        this.id = builder.id;
        this.customerId = builder.customerId;
        this.iban = builder.iban;
        this.balance = new Balance(builder.balance);
        this.createdOn = builder.createdOn;

    }


    public static class Builder {
        private String id;

        private String customerId;

        private Iban iban;
        private BigDecimal balance;


        private Instant createdOn;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withCustomerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder withIban(Iban iban) {
            this.iban = iban;
            return this;
        }


        public Builder withBalance(BigDecimal money) {
            this.balance = money;
            return this;
        }


        public Builder withCreatedOn(Instant createdOn) {
            this.createdOn = createdOn;
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
        balance = balance.subtract(transactionAmount);
    }

    public void clearBalance() {
        this.balance = Balance.initalBalance();
    }

    public Transaction recordDepositTransaction(String transactionId, Instant currentDate, BigDecimal transactionAmount, String senderAccountIdentifier, Bic senderAccountBic, String accountName, String reason) {
        return new Transaction(transactionId, currentDate, transactionAmount, senderAccountIdentifier, senderAccountBic.value(), accountName, reason);
    }

    public Transaction recordWithdrawalTransaction(String transactionId, Instant currentDate, BigDecimal transactionAmount, String receiverAccountIdentifier, Bic receiverAccountBic, String accountName, String reason) {
        return new Transaction(transactionId, currentDate, transactionAmount.negate(), receiverAccountIdentifier, receiverAccountBic.value(), accountName, reason);
    }


    public boolean hasEmptyBalance() {
        return balance.isEmpty();
    }

    public BigDecimal negativeBalance() {
        return balance.negate();
    }


    public String getId() {
        return id;
    }


    public String getCustomerId() {
        return customerId;
    }

    public String getIban() {
        return iban.value();
    }

    public BigDecimal getBalance() {
        return balance.value();
    }

    public Instant getCreationDate() {
        return createdOn;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(id, account.id) && Objects.equals(customerId, account.customerId) && Objects.equals(iban, account.iban) && Objects.equals(balance, account.balance) && Objects.equals(createdOn, account.createdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, iban, balance, createdOn);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", iban=" + iban +
                ", balance=" + balance +
                ", createdOn=" + createdOn +
                '}';
    }
}
