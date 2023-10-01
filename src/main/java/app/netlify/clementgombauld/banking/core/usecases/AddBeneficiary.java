package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.DuplicatedBeneficiaryException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.NoCurrentCustomerException;

import java.util.Optional;


public class AddBeneficiary {
    private final BeneficiaryRepository beneficiaryRepository;

    private final IdGenerator idGenerator;

    private final AuthenticationGateway authenticationGateway;

    public AddBeneficiary(BeneficiaryRepository beneficiaryRepository, IdGenerator idGenerator, AuthenticationGateway authenticationGateway) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.idGenerator = idGenerator;
        this.authenticationGateway = authenticationGateway;
    }

    public String handle(String beneficiaryIban, String beneficiaryBic, String beneficiaryName) {
        Customer currentCustomer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);
        Account account = currentCustomer.getAccount();
        Optional<Beneficiary> potentialDuplicatedBeneficiary = beneficiaryRepository.findByAccountIdAndIban(account.getId(), beneficiaryIban);
        potentialDuplicatedBeneficiary.ifPresent((beneficiary) -> {
            throw new DuplicatedBeneficiaryException(beneficiaryIban, account.getId());
        });
        String beneficiaryId = idGenerator.generate();
        beneficiaryRepository.insert(account.getId(), new Beneficiary(beneficiaryId, beneficiaryIban, beneficiaryBic, beneficiaryName));
        return beneficiaryId;
    }
}
