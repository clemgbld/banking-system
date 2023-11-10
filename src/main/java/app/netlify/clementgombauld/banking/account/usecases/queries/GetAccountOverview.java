package app.netlify.clementgombauld.banking.account.usecases.queries;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;

import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.UnknownAccountWithCustomerId;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountOverviewDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import org.iban4j.Iban;

import java.util.Objects;

public class GetAccountOverview {

    private static final int DEFAULT_LIMIT = 3;
    private final AuthenticationGateway authenticationGateway;

    private final QueryExecutor queryExecutor;

    public GetAccountOverview(AuthenticationGateway authenticationGateway, QueryExecutor queryExecutor) {
        this.authenticationGateway = authenticationGateway;
        this.queryExecutor = queryExecutor;
    }

    public AccountOverviewDto handle(Integer limit) {
        Customer customer = authenticationGateway.currentCustomer().orElseThrow(NoCurrentCustomerException::new);

        AccountWithTransactionsDto accountWithTransactionsDto = queryExecutor.findAccountWithTransactionsByCustomerId(new GetAccountOverviewQuery(
                        customer.getId(),
                        Objects.isNull(limit) ? DEFAULT_LIMIT : limit
                ))
                .orElseThrow(() -> new UnknownAccountWithCustomerId(customer.getId()));

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
