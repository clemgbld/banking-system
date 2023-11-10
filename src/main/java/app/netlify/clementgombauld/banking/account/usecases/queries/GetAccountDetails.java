package app.netlify.clementgombauld.banking.account.usecases.queries;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Bic;
import app.netlify.clementgombauld.banking.account.domain.Customer;
import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.UnknownAccountWithCustomerId;
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

        Bic validBic = new Bic(bic);

        Customer customer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);

        Iban iban = queryExecutor.findIbanByCustomerId(customer.getId())
                .orElseThrow(() -> new UnknownAccountWithCustomerId(customer.getId()));

        return new AccountDetailsDto(
                customer.getFirstName(),
                customer.getLastName(),
                iban.toString(),
                iban.getAccountNumber(),
                validBic.value()
        );
    }
}
