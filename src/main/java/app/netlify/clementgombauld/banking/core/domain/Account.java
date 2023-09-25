package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.DuplicatedBeneficiaryException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownBeneficiaryException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Account {

    private final Customer customer;

    private final String id;

    private final String iban;


    private Balance balance;

    private final List<MoneyTransferred> transactions;

    private List<Beneficiary> beneficiaries;


    private Account(Builder builder) {
        this.id = builder.id;
        this.iban = builder.iban;
        this.balance = new Balance(builder.balance);
        this.transactions = Optional.ofNullable(builder.transactions).orElse(new ArrayList<>());
        this.beneficiaries = builder.beneficiaries;
        this.customer = builder.customer;
    }


    public static class Builder {
        private Customer customer;
        private String id;
        private String iban;
        private BigDecimal balance;
        private List<MoneyTransferred> transactions;
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


        public Builder withTransactions(List<MoneyTransferred> transactions) {
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
        makeTransaction(transactionId, creationDate, transactionAmount, senderIban, senderAccountBic, senderAccountFullName);
    }

    public void withdraw(String transactionId, Instant creationDate, BigDecimal transactionAmount, String receiverAccountIban) {
        balance.checkBalanceSufficiency(transactionAmount);
        Beneficiary beneficiary = findBeneficiaryByIbanOrThrow(receiverAccountIban);
        makeTransaction(transactionId, creationDate, transactionAmount.negate(), receiverAccountIban, beneficiary.getBic(), beneficiary.getName());
    }


    public void addBeneficiary(String beneficiaryId, String beneficiaryIban, String beneficiaryBic, String beneficiaryName) {
        Beneficiary beneficiary = new Beneficiary(beneficiaryId, beneficiaryIban, beneficiaryBic, beneficiaryName);
        findBeneficiaryByIban(beneficiaryIban).ifPresent((b) -> {
            throw new DuplicatedBeneficiaryException(beneficiaryIban, id);
        });
        beneficiaries.add(beneficiary);
    }

    public void deleteBeneficiary(String beneficiaryIban) {
        findBeneficiaryByIbanOrThrow(beneficiaryIban);
        this.beneficiaries = beneficiaries.stream()
                .filter(b -> !b.hasIban(beneficiaryIban))
                .toList();
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
        transactions.add(new MoneyTransferred(transactionId, creationDate, transactionAmount, iban, bic, name));
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


    public List<MoneyTransferred> getTransactions() {
        return transactions;
    }


}
