package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownBeneficiaryException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class Account {

    private final Customer customer;

    private final String id;

    private final String iban;

    private Balance balance;

    private final List<Transaction> transactions;

    private final List<Beneficiary> beneficiaries;


    private Account(Builder builder) {
        this.id = builder.id;
        this.iban = builder.iban;
        this.balance = initBalance(builder.balance);
        this.transactions = Optional.ofNullable(builder.transactions).orElse(new ArrayList<>());
        this.beneficiaries = Optional.ofNullable(builder.beneficiaries).orElse(new ArrayList<>());
        this.customer = builder.customer;
    }


    public static class Builder {
        private Customer customer;
        private String id;
        private String iban;
        private BigDecimal balance;
        private List<Transaction> transactions;
        private List<Beneficiary> beneficiaries;


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


        public Builder withTransactions(List<Transaction> transactions) {
            this.transactions = transactions;
            return this;
        }

        public Builder withBeneficiaries(List<Beneficiary> beneficiaries) {
            this.beneficiaries = beneficiaries;
            return this;
        }

        public Builder withCustomer(Customer customer) {
            this.customer = customer;
            return this;
        }


        public Account build() {
            return new Account(this);
        }


    }


    public BigDecimal getBalance() {
        return balance.value();
    }

    public void deposit(String transactionId, Instant creationDate, BigDecimal transactionAmount, String senderIban, String senderAccountBic, String senderAccountFullName) {
        Optional<Beneficiary> optionalBeneficiary = findBeneficiaryByIban(senderIban);
        if (optionalBeneficiary.isPresent()) {
            Beneficiary beneficiary = optionalBeneficiary.get();
            makeTransaction(transactionId, creationDate, transactionAmount, beneficiary.getIban(), beneficiary.getBic(), beneficiary.getName());
            return;
        }
        makeTransaction(transactionId, creationDate, transactionAmount, senderIban, senderAccountBic, senderAccountFullName);

    }

    public void deposit(BigDecimal transactionAmount) {
        balance = balance.add(transactionAmount);
    }

    public void withdraw(String transactionId, Instant creationDate, BigDecimal transactionAmount, String receiverAccountIban) {
        balance.checkBalanceSufficiency(transactionAmount);
        Beneficiary beneficiary = findBeneficiaryByIbanOrThrow(receiverAccountIban);
        makeTransaction(transactionId, creationDate, transactionAmount.negate(), receiverAccountIban, beneficiary.getBic(), beneficiary.getName());
    }


    public Beneficiary findBeneficiaryByIbanOrThrow(String beneficiaryIban) {
        return findBeneficiaryByIban(beneficiaryIban).
                orElseThrow(() -> new UnknownBeneficiaryException(beneficiaryIban));
    }

    private Optional<Beneficiary> findBeneficiaryByIban(String beneficiaryIban) {
        return beneficiaries.stream()
                .filter(b -> b.hasIban(beneficiaryIban))
                .findFirst();
    }


    private void makeTransaction(String transactionId, Instant creationDate, BigDecimal transactionAmount, String iban, String bic, String name) {
        balance = balance.add(transactionAmount);
        transactions.add(new Transaction(transactionId, creationDate, transactionAmount, iban, bic, name));
    }

    public String getId() {
        return id;
    }


    public List<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    public Customer getCustomer() {
        return customer;
    }


    public String getIban() {
        return iban;
    }


    public List<Transaction> getTransactions() {
        return transactions;
    }

    private Balance initBalance(BigDecimal balance) {
        BigDecimal initialBalance = Optional.ofNullable(balance).orElse(new BigDecimal(0));
        return new Balance(initialBalance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(id, account.id) && Objects.equals(iban, account.iban) && Objects.equals(balance, account.balance) && Objects.equals(transactions, account.transactions) && Objects.equals(beneficiaries, account.beneficiaries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, iban, balance, transactions, beneficiaries);
    }

    @Override
    public String toString() {
        return "Account{" +
                "customer=" + customer +
                ", id='" + id + '\'' +
                ", accountIdentifier='" + iban + '\'' +
                ", balance=" + balance +
                ", transactions=" + transactions +
                ", beneficiaries=" + beneficiaries +
                '}';
    }
}
