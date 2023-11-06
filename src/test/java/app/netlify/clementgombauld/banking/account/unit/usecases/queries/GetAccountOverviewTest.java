package app.netlify.clementgombauld.banking.account.unit.usecases.queries;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;
import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountOverviewDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.TransactionDto;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryQueryExecutor;
import app.netlify.clementgombauld.banking.account.usecases.queries.AccountQuery;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverview;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;

public class GetAccountOverviewTest {


    @Test
    void shouldGetAccountOverviewTest() {
        String customerId = "56";
        String firstName = "John";
        String lastName = "Smith";
        String iban = "FR1420041010050500013M02606";
        String accountNumber = "0500013M026";
        String accountName = "Michel Baumont";
        BigDecimal balance = new BigDecimal("5.00");
        String reason = "shopping";
        Instant creationDate = Instant.ofEpochSecond(95345L);
        int limit = 5;

        List<TransactionDto> transactionsDTO = List.of(
                new TransactionDto(
                        accountName,
                        creationDate,
                        new BigDecimal("5.00"),
                        reason
                )
        );

        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(new Customer(customerId, firstName, lastName));

        QueryExecutor queryExecutor = new InMemoryQueryExecutor(
                Map.of(
                        new AccountQuery(customerId, limit),
                        new AccountWithTransactionsDto(
                                iban,
                                balance,
                                transactionsDTO
                        )
                )
        );

        GetAccountOverview getAccountOverview = new GetAccountOverview(authenticationGateway, queryExecutor);


        AccountOverviewDto actualAccountOverviewDto = getAccountOverview.handle(limit);


        assertThat(actualAccountOverviewDto).isEqualTo(new AccountOverviewDto(
                firstName,
                lastName,
                accountNumber,
                balance,
                transactionsDTO
        ));
    }

}
