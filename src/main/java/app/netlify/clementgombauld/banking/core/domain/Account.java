package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.DuplicatedBeneficiaryException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownBeneficiaryException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Account {

    private final String id;

    private final String iban;

    private final String bic;

    private Balance balance;

    private final String firstName;

    private final String lastName;

    private final List<MoneyTransferred> transactions;

    private final List<Beneficiary> beneficiaries;


    private Account(Builder builder) {
        this.id = builder.id;
        this.iban = builder.iban;
        this.bic = builder.bic;
        this.balance = new Balance(builder.balance);
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.transactions = Optional.ofNullable(builder.transactions).orElse(new ArrayList<>());
        this.beneficiaries = builder.beneficiaries;
    }




    public static class Builder {
        private String id;
        private  String iban;
        private  String bic;
        private BigDecimal balance;
        private String firstName;
        private String lastName;
        private List<MoneyTransferred> transactions;
        private List<Beneficiary> beneficiaries;



        public  Builder withId(String id){
            this.id = id;
            return this;
        }

        public Builder withIban(String iban){
            this.iban = iban;
            return this;
        }

        public Builder withBic(String bic){
            this.bic = bic;
            return this;
        }

        public Builder withBalance(BigDecimal money){
            this.balance = money;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
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

        public Account build() {
            return new Account(this);
        }
    }



    public BigDecimal getBalance() {
        return balance.value();
    }

    public void deposit(String transactionId, Instant creationDate, BigDecimal transactionAmount, String senderIban, String firstName, String lastName) {
       makeTransaction(transactionId,creationDate,transactionAmount,senderIban,buildFullName(firstName,lastName));
    }
    public void withdraw(String transactionId, Instant creationDate, BigDecimal transactionAmount,String receiverIban) {
        balance.checkBalanceSufficiency(transactionAmount);
        Beneficiary beneficiary = findBeneficiary(receiverIban);
        makeTransaction(transactionId,creationDate,transactionAmount.negate(), receiverIban,beneficiary.getName());
    }

    public boolean isInDifferentBank(String receiverAccountIban) {
        Beneficiary beneficiary = findBeneficiary(receiverAccountIban);
        return beneficiary.isInDifferentBank(bic);

    }

    public void addBeneficiary(String beneficiaryId, String beneficiaryIban, String beneficiaryBic, String beneficiaryName) {
        Beneficiary beneficiary = new Beneficiary(beneficiaryId,beneficiaryIban,beneficiaryBic,beneficiaryName);
        beneficiaries.stream().filter(b -> b.hasIban(beneficiary.getIban())).findFirst().ifPresent((b)-> {
            throw new DuplicatedBeneficiaryException(beneficiaryIban,id);
        });
        beneficiaries.add(beneficiary);
    }

    private Beneficiary findBeneficiary(String beneficiaryIban) {
        return beneficiaries.stream()
                .filter(b -> b.hasIban(beneficiaryIban))
                .findFirst()
                .orElseThrow(()-> new UnknownBeneficiaryException(beneficiaryIban));
    }


    public String getIban() {
        return iban;
    }


    public List<MoneyTransferred> getTransactions() {
        return transactions;
    }

    private void makeTransaction(String transactionId, Instant creationDate,BigDecimal transactionAmount,String iban,String name){
        balance = balance.add(transactionAmount);
        transactions.add(new MoneyTransferred(transactionId,creationDate,transactionAmount,iban,name));
    }

    public String getId() {
        return id;
    }

    public String getBic() {
        return bic;
    }


    public String getFullName(){
        return buildFullName(firstName,lastName);
    }

    private String buildFullName(String firstName,String lastName){
        return String.format("%s %s",firstName,lastName);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }


}
