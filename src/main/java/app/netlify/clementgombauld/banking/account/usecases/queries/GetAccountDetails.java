package app.netlify.clementgombauld.banking.account.usecases.queries;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;
import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountDetailsDto;
import org.iban4j.Iban;

public class GetAccountDetails {

    private final AuthenticationGateway authenticationGateway;

    private final QueryExecutor queryExecutor;

    public GetAccountDetails(AuthenticationGateway authenticationGateway, QueryExecutor queryExecutor) {
        this.authenticationGateway = authenticationGateway;
        this.queryExecutor = queryExecutor;
    }

    public AccountDetailsDto handle(String bic) {

        Customer customer = authenticationGateway.currentCustomer().orElseThrow();

        Iban iban = queryExecutor.findIbanByCustomerId(customer.getId()).orElseThrow();

        return new AccountDetailsDto(
                customer.getFirstName(),
                customer.getLastName(),
                iban.toString(),
                iban.getAccountNumber(),
                bic
        );
    }
}
