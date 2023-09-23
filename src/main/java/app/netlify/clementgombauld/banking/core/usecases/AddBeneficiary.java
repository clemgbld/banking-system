package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.NoCurrentCustomerException;



public class AddBeneficiary {
    private final AccountRepository accountRepository;

    private final IdGenerator idGenerator;

    private final AuthenticationGateway authenticationGateway;

    public AddBeneficiary(AccountRepository accountRepository,IdGenerator idGenerator,AuthenticationGateway authenticationGateway) {
        this.accountRepository = accountRepository;
        this.idGenerator = idGenerator;
        this.authenticationGateway = authenticationGateway;
    }

    public String handle( String beneficiaryIban, String beneficiaryBic, String beneficiaryName) {
        Customer currentCustomer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);
        Account account = currentCustomer.getAccount();
        String beneficiaryId = idGenerator.generate();
        account.addBeneficiary(beneficiaryId,beneficiaryIban,beneficiaryBic,beneficiaryName);
        accountRepository.update(account);
        return beneficiaryId;
    }
}
