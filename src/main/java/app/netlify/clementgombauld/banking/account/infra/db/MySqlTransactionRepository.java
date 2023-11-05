package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.domain.Transaction;
import app.netlify.clementgombauld.banking.account.domain.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MySqlTransactionRepository implements TransactionRepository {


    private final JpaTransactionRepository jpaTransactionRepository;

    @Autowired
    public MySqlTransactionRepository(JpaTransactionRepository jpaTransactionRepository) {
        this.jpaTransactionRepository = jpaTransactionRepository;
    }

    @Override
    public void insert(String accountId, Transaction transaction) {
        jpaTransactionRepository.insertByAccountId(transaction.id(), transaction.creationDate(), transaction.transactionAmount(), transaction.accountIdentifier(), transaction.bic(), transaction.accountName(), transaction.reason(), accountId);
    }
}
