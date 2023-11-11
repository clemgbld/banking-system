package app.netlify.clementgombauld.banking.account.unit.usecases.queries;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;
import app.netlify.clementgombauld.banking.account.rest.account.out.PageDto;
import app.netlify.clementgombauld.banking.account.usecases.queries.QueryExecutor;
import app.netlify.clementgombauld.banking.account.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.account.rest.account.out.TransactionDto;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryQueryExecutor;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetTransactions;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetTransactionsQuery;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GetTransactionsTest {


    @Test
    void shouldGetPaginatedTransactions() {
        String customerId = "56";
        String firstName = "firstName";
        String lastName = "lastName";
        String transactionId = "1";
        String accountName = "Arsene Lupin";
        long creationDate = 2534523343L;
        BigDecimal transactionAmount = new BigDecimal("7.98");
        String reason = "shopping";
        int pageNumber = 1;
        int pageSize = 10;

        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(new Customer(customerId, firstName, lastName));

        TransactionDto transactionDto = new TransactionDto(transactionId, accountName, creationDate, transactionAmount, reason);
        QueryExecutor queryExecutor = new InMemoryQueryExecutor(Map.of(new GetTransactionsQuery(customerId, pageNumber, pageSize), List.of(transactionDto)));

        GetTransactions getTransactions = new GetTransactions(authenticationGateway, queryExecutor);

        PageDto<TransactionDto> transactions = getTransactions.handle(pageNumber, pageSize);

        assertThat(transactions).isEqualTo(new PageDto<>(List.of(transactionDto), pageNumber, pageSize, 1L, 1));
    }

    @Test
    void shouldGetPaginationTransactionsWithDefaultPageSizeAndPageNumber() {
        String customerId = "56";
        String firstName = "firstName";
        String lastName = "lastName";
        String transactionId = "1";
        String accountName = "Arsene Lupin";
        long creationDate = 2534523343L;
        BigDecimal transactionAmount = new BigDecimal("7.98");
        String reason = "shopping";
        int pageNumber = 1;
        int pageSize = 10;

        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(new Customer(customerId, firstName, lastName));

        TransactionDto transactionDto = new TransactionDto(transactionId, accountName, creationDate, transactionAmount, reason);
        QueryExecutor queryExecutor = new InMemoryQueryExecutor(Map.of(new GetTransactionsQuery(customerId, pageNumber, pageSize), List.of(transactionDto)));

        GetTransactions getTransactions = new GetTransactions(authenticationGateway, queryExecutor);

        PageDto<TransactionDto> transactions = getTransactions.handle(null, null);

        assertThat(transactions).isEqualTo(new PageDto<>(List.of(transactionDto), pageNumber, pageSize, 1L, 1));
    }

    @Test
    void shouldNotGetAnyTransactionsWhenThereIsNoCustomerAuthenticated() {
        GetTransactions getTransactions = new GetTransactions(new InMemoryAuthenticationGateway(null), null);
        assertThatThrownBy(() -> getTransactions.handle(1, 1))
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");
    }
}
