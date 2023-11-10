package app.netlify.clementgombauld.banking.account.unit.usecases.queries;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;
import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.domain.exceptions.InvalidBicException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.UnknownAccountWithCustomerId;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountDetailsDto;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryQueryExecutor;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountDetails;
import org.iban4j.Iban;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GetAccountDetailsTest {


    @Test
    void shouldGetAccountDetails() {
        String customerId = "56";
        String firstName = "John";
        String lastName = "Smith";
        String iban = "FR1420041010050500013M02606";
        String accountNumber = "0500013M026";

        String bic = "AGRIFRPP989";


        GetAccountDetails getAccountDetails = buildGetAccountDetails(new Customer(
                customerId,
                firstName,
                lastName
        ), customerId, iban);

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

    @Test
    void shouldNotGetAnyAccountDetailsWhenThereIsNoCustomerAuthenticated() {
        GetAccountDetails getAccountDetails = buildGetAccountDetails(null, "", "FR1420041010050500013M02606");
        assertThatThrownBy(() -> getAccountDetails.handle("AGRIFRPP989"))
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");
    }

    @Test
    void shouldNotGetAnyAccountDetailsWhenThereIsNoAccountIbanFound() {
        String customerId = "3";
        GetAccountDetails getAccountDetails = buildGetAccountDetails(new Customer(customerId, "Arsene", "Lupin"), "2", "FR1420041010050500013M02606");
        assertThatThrownBy(() -> getAccountDetails.handle("AGRIFRPP989"))
                .isInstanceOf(UnknownAccountWithCustomerId.class)
                .hasMessage("There is no account with the customerId: " + customerId);
    }

    @Test
    void shouldNotGetAnyAccountDetailsWhenThereIsAnInvalidBic() {
        String customerId = "3";
        GetAccountDetails getAccountDetails = buildGetAccountDetails(new Customer(customerId, "Arsene", "Lupin"), "2", "FR1420041010050500013M02606");
        assertThatThrownBy(() -> getAccountDetails.handle("AGRIFR"))
                .isInstanceOf(InvalidBicException.class)
                .hasMessage("bic: AGRIFR is invalid.");
    }

    private GetAccountDetails buildGetAccountDetails(Customer customer, String customerId, String iban) {
        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(
                customer
        );
        QueryExecutor queryExecutor = new InMemoryQueryExecutor(Map.of(
                customerId,
                Iban.valueOf(iban)
        ));
        return new GetAccountDetails(authenticationGateway, queryExecutor);
    }


}
