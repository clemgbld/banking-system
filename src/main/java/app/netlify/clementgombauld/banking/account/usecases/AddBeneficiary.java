package app.netlify.clementgombauld.banking.account.usecases;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.domain.exceptions.DuplicatedBeneficiaryException;
import app.netlify.clementgombauld.banking.account.usecases.commands.AddBeneficiaryCommand;
import app.netlify.clementgombauld.banking.common.domain.IdGenerator;


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

    public String handle(AddBeneficiaryCommand addBeneficiaryCommand) {
        Account account = customerAccountFinder.findAccount();
        Optional<Beneficiary> potentialDuplicatedBeneficiary = beneficiaryRepository.findByAccountIdAndIban(account.getId(), addBeneficiaryCommand.beneficiaryIban());
        potentialDuplicatedBeneficiary.ifPresent((beneficiary) -> {
            throw new DuplicatedBeneficiaryException(addBeneficiaryCommand.beneficiaryIban(), account.getId());
        });
        String beneficiaryId = idGenerator.generate();
        beneficiaryRepository.insert(account.getId(), new Beneficiary(beneficiaryId, new Iban(addBeneficiaryCommand.beneficiaryIban()), new Bic(addBeneficiaryCommand.beneficiaryBic()), addBeneficiaryCommand.beneficiaryName()));
        return beneficiaryId;
    }
}
