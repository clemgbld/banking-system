package app.netlify.clementgombauld.banking.account.unit.usecases.queries;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;
import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.out.BeneficiaryDto;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryQueryExecutor;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetBeneficiaries;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class GetBeneficiariesTest {
    @Test
    void shouldGetBeneficiaries() {
        String customerId = "56";
        String firstName = "John";
        String lastName = "Smith";
        String id = "5";
        String iban = "FR1420041010050500013M02606";
        String bic = "BNPAFRPP123";
        String accountName = "Arsene Lupin";

        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(new Customer(customerId, firstName, lastName));

        QueryExecutor queryExecutor = new InMemoryQueryExecutor(Map.of(
                customerId, List.of(new BeneficiaryDto(
                        id, iban, bic, accountName
                ))
        ));

        GetBeneficiaries getBeneficiaries = new GetBeneficiaries(authenticationGateway, queryExecutor);

        List<BeneficiaryDto> beneficiaries = getBeneficiaries.handle();

        assertThat(beneficiaries).isEqualTo(List.of(new BeneficiaryDto(
                id, iban, bic, accountName
        )));
    }

    @Test
    void shouldNotGetAnyBeneficiariesWhenTheCustomerIsNotAuthenticated() {
        GetBeneficiaries getBeneficiaries = new GetBeneficiaries(new InMemoryAuthenticationGateway(null), null);
        assertThatThrownBy(getBeneficiaries::handle)
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");
    }
}
