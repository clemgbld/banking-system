package app.netlify.clementgombauld.banking.core.domain;

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

    public Account(String id, String iban, String bic, BigDecimal balance, String firstName, String lastName, List<MoneyTransferred> transactions, List<Beneficiary> beneficiaries) {
        this.id = id;
        this.iban = iban;
        this.bic = bic;
        this.balance = new Balance(balance);
        this.firstName = firstName;
        this.lastName = lastName;
        this.transactions = Optional.ofNullable(transactions)
                .orElse(new ArrayList<>());
        this.beneficiaries = beneficiaries;
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


}
