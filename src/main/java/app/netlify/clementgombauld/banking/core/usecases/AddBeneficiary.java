package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.DuplicatedBeneficiaryException;


import java.util.Optional;


public class AddBeneficiary {
    private final BeneficiaryRepository beneficiaryRepository;

    private final IdGenerator idGenerator;

    private final CustomerAccountFinder customerAccountFinder;

    public AddBeneficiary(BeneficiaryRepository beneficiaryRepository, IdGenerator idGenerator, AuthenticationGateway authenticationGateway, AccountRepository accountRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.idGenerator = idGenerator;
        this.customerAccountFinder = new CustomerAccountFinder(authenticationGateway, accountRepository);
    }

    public String handle(String beneficiaryIban, String beneficiaryBic, String beneficiaryName) {
        Account account = customerAccountFinder.findAccount();
        Optional<Beneficiary> potentialDuplicatedBeneficiary = beneficiaryRepository.findByAccountIdAndIban(account.getId(), beneficiaryIban);
        potentialDuplicatedBeneficiary.ifPresent((beneficiary) -> {
            throw new DuplicatedBeneficiaryException(beneficiaryIban, account.getId());
        });
        String beneficiaryId = idGenerator.generate();
        beneficiaryRepository.insert(account.getId(), new Beneficiary(beneficiaryId, new Iban(beneficiaryIban), new Bic(beneficiaryBic), beneficiaryName));
        return beneficiaryId;
    }
}
