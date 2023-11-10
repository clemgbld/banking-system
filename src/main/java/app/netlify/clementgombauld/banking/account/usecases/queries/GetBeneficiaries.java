package app.netlify.clementgombauld.banking.account.usecases.queries;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;
import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.out.BeneficiaryDto;

import java.util.List;

public class GetBeneficiaries {

    private final AuthenticationGateway authenticationGateway;

    private final QueryExecutor queryExecutor;

    public GetBeneficiaries(AuthenticationGateway authenticationGateway, QueryExecutor queryExecutor) {
        this.authenticationGateway = authenticationGateway;
        this.queryExecutor = queryExecutor;
    }

    public List<BeneficiaryDto> handle() {
        Customer customer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);
        return queryExecutor.findBeneficiariesByCustomerId(customer.getId());
    }
}
