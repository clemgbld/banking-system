package app.netlify.clementgombauld.banking.account.unit.usecases.queries;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;
import app.netlify.clementgombauld.banking.account.usecases.queries.QueryExecutor;
import app.netlify.clementgombauld.banking.account.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.UnknownAccountWithCustomerId;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountOverviewDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.TransactionDto;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryQueryExecutor;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverviewQuery;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverview;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GetAccountOverviewTest {


    @Test
    void shouldGetAccountOverview() {
        String transactionId = "2534212";
        String customerId = "56";
        String firstName = "John";
        String lastName = "Smith";
        String iban = "FR1420041010050500013M02606";
        String accountNumber = "0500013M026";
        String accountName = "Michel Baumont";
        BigDecimal balance = new BigDecimal("5.00");
        String reason = "shopping";
        long creationDate = 95345000L;
        int limit = 5;

        List<TransactionDto> transactionsDTO = List.of(
                new TransactionDto(
                        transactionId,
                        accountName,
                        creationDate,
                        new BigDecimal("5.00"),
                        reason
                )
        );


        GetAccountOverview getAccountOverview = buildGetAccountOverview(new Customer(customerId, firstName, lastName), new AccountWithTransactionsDto(
                iban,
                balance,
                transactionsDTO
        ), new GetAccountOverviewQuery(customerId, limit));


        AccountOverviewDto actualAccountOverviewDto = getAccountOverview.handle(limit);


        assertThat(actualAccountOverviewDto).isEqualTo(new AccountOverviewDto(
                firstName,
                lastName,
                accountNumber,
                balance,
                transactionsDTO
        ));
    }

    @Test
    void shouldHaveADefaultLimitWhenThereIsNone() {
        String transactionId = "2534212";
        String customerId = "56";
        String firstName = "John";
        String lastName = "Smith";
        String iban = "FR1420041010050500013M02606";
        String accountNumber = "0500013M026";
        String accountName = "Michel Baumont";
        BigDecimal balance = new BigDecimal("5.00");
        String reason = "shopping";
        long creationDate = 95345000L;
        List<TransactionDto> transactionsDTO = List.of(
                new TransactionDto(
                        transactionId,
                        accountName,
                        creationDate,
                        new BigDecimal("5.00"),
                        reason
                )
        );

        GetAccountOverview getAccountOverview = buildGetAccountOverview(new Customer(customerId, firstName, lastName), new AccountWithTransactionsDto(
                iban,
                balance,
                transactionsDTO
        ), new GetAccountOverviewQuery(customerId, 3));


        AccountOverviewDto actualAccountOverviewDto = getAccountOverview.handle(null);


        assertThat(actualAccountOverviewDto).isEqualTo(new AccountOverviewDto(
                firstName,
                lastName,
                accountNumber,
                balance,
                transactionsDTO
        ));
    }


    @Test
    void shouldThrowWhenThereIsNoCurrentCustomer() {
        GetAccountOverview getAccountOverview = buildGetAccountOverview(null, new AccountWithTransactionsDto(
                "",
                new BigDecimal("5.00"),
                List.of()
        ), new GetAccountOverviewQuery("5", 3));
        assertThatThrownBy(() -> getAccountOverview.handle(3))
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");
    }

    @Test
    void shouldThrowWhenThereIsNoAccountFond() {
        String customerId = "7";
        GetAccountOverview getAccountOverview = buildGetAccountOverview(new Customer(customerId, "John", "Smith"), new AccountWithTransactionsDto(
                "",
                new BigDecimal("5.00"),
                List.of()
        ), new GetAccountOverviewQuery("5", 3));
        assertThatThrownBy(() -> getAccountOverview.handle(3))
                .isInstanceOf(UnknownAccountWithCustomerId.class)
                .hasMessage("There is no account with the customerId: " + customerId);
    }

    private GetAccountOverview buildGetAccountOverview(Customer customer, AccountWithTransactionsDto accountWithTransactionsDto, GetAccountOverviewQuery getAccountOverviewQuery) {
        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(customer);
        QueryExecutor queryExecutor = new InMemoryQueryExecutor(
                Map.of(
                        getAccountOverviewQuery,
                        accountWithTransactionsDto
                )
        );
        return new GetAccountOverview(authenticationGateway, queryExecutor);
    }


}
