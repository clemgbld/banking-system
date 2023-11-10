package app.netlify.clementgombauld.banking.account.unit.usecases.queries;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;
import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountDetailsDto;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryQueryExecutor;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountDetails;
import org.iban4j.Iban;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GetAccountDetailsTest {


    @Test
    void shouldGetAccountDetails() {
        String customerId = "56";
        String firstName = "John";
        String lastName = "Smith";
        String iban = "FR1420041010050500013M02606";
        String accountNumber = "0500013M026";

        String bic = "AGRIFRPP989";

        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(
                new Customer(
                        customerId,
                        firstName,
                        lastName
                )

        );

        QueryExecutor queryExecutor = new InMemoryQueryExecutor(Map.of(
                customerId,
                Iban.valueOf("FR1420041010050500013M02606")
        ));

        GetAccountDetails getAccountDetails = new GetAccountDetails(authenticationGateway, queryExecutor);

        AccountDetailsDto accountDetailsDto = getAccountDetails.handle(bic);

        assertThat(accountDetailsDto).isEqualTo(
                new AccountDetailsDto(
                        firstName,
                        lastName,
                        iban,
                        accountNumber,
                        bic
                )
        );
    }
}
