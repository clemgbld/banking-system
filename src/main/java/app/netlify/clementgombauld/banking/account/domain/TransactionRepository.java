package app.netlify.clementgombauld.banking.account.domain;

public interface TransactionRepository {
    void insert(String accountId, Transaction transaction);
}
