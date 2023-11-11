package app.netlify.clementgombauld.banking.account.usecases.queries;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;
import app.netlify.clementgombauld.banking.account.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.account.rest.account.out.PageDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.TransactionDto;


import java.util.Optional;

public class GetTransactions {

    private final AuthenticationGateway authenticationGateway;

    private final QueryExecutor queryExecutor;

    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    public GetTransactions(AuthenticationGateway authenticationGateway, QueryExecutor queryExecutor) {
        this.authenticationGateway = authenticationGateway;
        this.queryExecutor = queryExecutor;
    }

    public PageDto<TransactionDto> handle(Integer pageNumber, Integer pageSize) {

        Customer customer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);


        return queryExecutor.findTransactionsByCustomerId(new GetTransactionsQuery(customer.getId(), Optional.ofNullable(pageNumber).orElse(DEFAULT_PAGE_NUMBER), Optional.ofNullable(pageSize).orElse(
                DEFAULT_PAGE_SIZE)
        ));
    }

}
