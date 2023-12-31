package app.netlify.clementgombauld.banking.account.unit.inmemory;

import app.netlify.clementgombauld.banking.account.domain.Transaction;
import app.netlify.clementgombauld.banking.account.domain.TransactionRepository;

import java.util.Map;

public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<String, Transaction> transactionStore;

    public InMemoryTransactionRepository(Map<String, Transaction> transactionStore) {
        this.transactionStore = transactionStore;
    }

    @Override
    public void insert(String accountId, Transaction transaction) {
        transactionStore.put(accountId, transaction);
    }
}
