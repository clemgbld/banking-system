package app.netlify.clementgombauld.banking.account.domain;

import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverviewQuery;
import org.iban4j.Iban;

import java.util.Optional;

public interface QueryExecutor {

    Optional<AccountWithTransactionsDto> getAccountWithTransactions(GetAccountOverviewQuery query);

    Optional<Iban> findIbanByCustomerId(String customerId);
}
