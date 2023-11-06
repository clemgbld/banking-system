package app.netlify.clementgombauld.banking.account.domain;

import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.usecases.queries.AccountQuery;

import java.util.Optional;

public interface QueryExecutor {

    Optional<AccountWithTransactionsDto> getAccountWithTransactions(AccountQuery query);
}
