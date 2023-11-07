package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverviewQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MySqlQueryExecutor implements QueryExecutor {
    @Override
    public Optional<AccountWithTransactionsDto> getAccountWithTransactions(GetAccountOverviewQuery query) {
        return Optional.empty();
    }
}
