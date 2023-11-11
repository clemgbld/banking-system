package app.netlify.clementgombauld.banking.account.usecases.commands;


import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.domain.exceptions.UnknownBeneficiaryException;
import jakarta.transaction.Transactional;


public class DeleteBeneficiary {

    private final BeneficiaryRepository beneficiaryRepository;

    private final CustomerAccountFinder customerAccountFinder;

    public DeleteBeneficiary(BeneficiaryRepository beneficiaryRepository, AuthenticationGateway authenticationGateway, AccountRepository accountRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.customerAccountFinder = new CustomerAccountFinder(authenticationGateway, accountRepository);
    }

    public void handle(String beneficiaryIban) {
        Iban validBeneficiaryIban = new Iban(beneficiaryIban);
        Account account = customerAccountFinder.findAccount();
        beneficiaryRepository.findByAccountIdAndIban(account.getId(), validBeneficiaryIban.value())
                .orElseThrow(() -> {
                    throw new UnknownBeneficiaryException(validBeneficiaryIban.value());
                });
        beneficiaryRepository.delete(account.getId(), validBeneficiaryIban.value());
    }
}
