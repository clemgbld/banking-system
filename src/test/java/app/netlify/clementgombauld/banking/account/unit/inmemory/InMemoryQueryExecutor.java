package app.netlify.clementgombauld.banking.account.unit.inmemory;

import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.usecases.queries.AccountQuery;

import java.util.Map;
import java.util.Optional;

public class InMemoryQueryExecutor implements QueryExecutor {
    private final Map<AccountQuery, Object> store;

    public InMemoryQueryExecutor(Map<AccountQuery, Object> store) {
        this.store = store;
    }

    @Override
    public Optional<AccountWithTransactionsDto> getAccountWithTransactions(AccountQuery query) {
        return Optional.ofNullable((AccountWithTransactionsDto) store.get(query));
    }
}
