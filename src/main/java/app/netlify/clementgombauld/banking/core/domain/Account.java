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

    private List<Transaction> transactions;

    public Account(String id,String iban, String bic, BigDecimal balance,List<Transaction> transactions) {
        this.id = id;
        this.iban = iban;
        this.bic = bic;
        this.balance = balance;
        this.transactions = Optional.ofNullable(transactions)
                .orElse(new ArrayList<>());
    }



    public BigDecimal getBalance() {
        return balance;
    }

    public void credit(String transactionId, Instant creationDate,BigDecimal transactionAmount) {
       makeTransaction(transactionId,creationDate,transactionAmount);
    }
    public void withdraw(String transactionId, Instant creationDate, BigDecimal transactionAmount) {
        makeTransaction(transactionId,creationDate,transactionAmount.negate());
    }



    public String getIban() {
        return iban;
    }


    public List<Transaction> getTransactions() {
        return transactions;
    }

    private void makeTransaction(String transactionId, Instant creationDate,BigDecimal transactionAmount){
        balance = balance.add(transactionAmount);
        transactions.add(new Transaction(transactionId,creationDate,transactionAmount));
    }


}
