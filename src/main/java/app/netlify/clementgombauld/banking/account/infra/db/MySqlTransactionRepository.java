package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.domain.Transaction;
import app.netlify.clementgombauld.banking.account.domain.TransactionRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MySqlTransactionRepository implements TransactionRepository {
    @Override
    public void insert(String accountId, Transaction transaction) {

    }
}
