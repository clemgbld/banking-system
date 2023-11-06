package app.netlify.clementgombauld.banking.account.usecases.queries;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;

import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountOverviewDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import org.iban4j.Iban;

public class GetAccountOverview {

    private final AuthenticationGateway authenticationGateway;

    private final QueryExecutor queryExecutor;

    public GetAccountOverview(AuthenticationGateway authenticationGateway, QueryExecutor queryExecutor) {
        this.authenticationGateway = authenticationGateway;
        this.queryExecutor = queryExecutor;
    }

    public AccountOverviewDto handle(int limit) {
        Customer customer = authenticationGateway.currentCustomer().orElseThrow();

        AccountWithTransactionsDto accountWithTransactionsDto = queryExecutor.getAccountWithTransactions(new AccountQuery(
                        customer.getId(),
                        limit
                ))
                .orElseThrow();

        Iban iban = Iban.valueOf(accountWithTransactionsDto.iban());

        return new AccountOverviewDto(
                customer.getFirstName(),
                customer.getLastName(),
                iban.getAccountNumber(),
                accountWithTransactionsDto.balance(),
                accountWithTransactionsDto.transactionsDto()
        );
    }
}
