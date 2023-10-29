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

    public String handle(AddBeneficiaryCommand command) {
        Account account = customerAccountFinder.findAccount();
        Optional<Beneficiary> potentialDuplicatedBeneficiary = beneficiaryRepository.findByAccountIdAndIban(account.getId(), command.beneficiaryIban());
        potentialDuplicatedBeneficiary.ifPresent((beneficiary) -> {
            throw new DuplicatedBeneficiaryException(command.beneficiaryIban(), account.getId());
        });
        String beneficiaryId = idGenerator.generate();
        beneficiaryRepository.insert(account.getId(), new Beneficiary(beneficiaryId, new Iban(command.beneficiaryIban()), new Bic(command.beneficiaryBic()), command.beneficiaryName()));
        return beneficiaryId;
    }
}
