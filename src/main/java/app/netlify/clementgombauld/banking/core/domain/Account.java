package app.netlify.clementgombauld.banking.core.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Account {

    private final String id;

    private final String iban;

    private final String bic;

    private BigDecimal balance;

    private String firstName;

    private String lastName;

    private final List<Transaction> transactions;

    private final List<Beneficiary> beneficiaries;

    public Account(String id, String iban, String bic, BigDecimal balance, String firstName, String lastName, List<Transaction> transactions, List<Beneficiary> beneficiaries) {
        this.id = id;
        this.iban = iban;
        this.bic = bic;
        this.balance = balance;
        this.firstName = firstName;
        this.lastName = lastName;
        this.transactions = Optional.ofNullable(transactions)
                .orElse(new ArrayList<>());
        this.beneficiaries = beneficiaries;
    }



    public BigDecimal getBalance() {
        return balance;
    }

    public void credit(String transactionId, Instant creationDate,BigDecimal transactionAmount,String senderIban,String firstName,String lastName) {
       makeTransaction(transactionId,creationDate,transactionAmount,senderIban,firstName,lastName);
    }
    public void withdraw(String transactionId, Instant creationDate, BigDecimal transactionAmount,String receiverIban) {
        Beneficiary beneficiary = beneficiaries.stream().filter(b -> receiverIban.equals(b.getIban())).findFirst().orElseThrow(RuntimeException::new);
        makeTransaction(transactionId,creationDate,transactionAmount.negate(), receiverIban,beneficiary.getFirstName(),beneficiary.getLastName());
    }



    public String getIban() {
        return iban;
    }


    public List<Transaction> getTransactions() {
        return transactions;
    }

    private void makeTransaction(String transactionId, Instant creationDate,BigDecimal transactionAmount,String iban,String firstName,String lastName){
        balance = balance.add(transactionAmount);
        transactions.add(new Transaction(transactionId,creationDate,transactionAmount,iban,firstName,lastName));
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
