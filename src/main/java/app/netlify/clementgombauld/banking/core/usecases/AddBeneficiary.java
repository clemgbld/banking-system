package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.NoCurrentCustomerException;


public class AddBeneficiary {
    private final AccountRepository accountRepository;

    private final BeneficiaryRepository beneficiaryRepository;

    private final IdGenerator idGenerator;

    private final AuthenticationGateway authenticationGateway;

    public AddBeneficiary(AccountRepository accountRepository, BeneficiaryRepository beneficiaryRepository, IdGenerator idGenerator, AuthenticationGateway authenticationGateway) {
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.idGenerator = idGenerator;
        this.authenticationGateway = authenticationGateway;
    }

    public String handle(String beneficiaryIban, String beneficiaryBic, String beneficiaryName) {
        Customer currentCustomer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);
        Account account = currentCustomer.getAccount();
        String beneficiaryId = idGenerator.generate();
        beneficiaryRepository.insert(account.getId(), new Beneficiary(beneficiaryId, beneficiaryIban, beneficiaryBic, beneficiaryName));
        return beneficiaryId;
    }
}
