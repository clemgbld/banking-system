package app.netlify.clementgombauld.banking.account.unit.inmemory;

import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverviewQuery;
import org.iban4j.Iban;

import java.util.Map;
import java.util.Optional;

public class InMemoryQueryExecutor implements QueryExecutor {
    private final Map<Object, Object> store;

    public InMemoryQueryExecutor(Map<Object, Object> store) {
        this.store = store;
    }

    @Override
    public Optional<AccountWithTransactionsDto> getAccountWithTransactions(GetAccountOverviewQuery query) {
        return Optional.ofNullable((AccountWithTransactionsDto) store.get(query));
    }

    @Override
    public Optional<Iban> findIbanByCustomerId(String customerId) {
        return Optional.ofNullable((Iban) store.get(customerId));
    }
}
