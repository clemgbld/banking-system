package app.netlify.clementgombauld.banking.core.domain;

public interface TransactionRepository {
    void insert(String accountId, Transaction transaction);
}
