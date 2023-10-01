package app.netlify.clementgombauld.banking.core.usecases;


import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownBeneficiaryException;


public class DeleteBeneficiary {

    private final BeneficiaryRepository beneficiaryRepository;
    private final AuthenticationGateway authenticationGateway;

    public DeleteBeneficiary(BeneficiaryRepository beneficiaryRepository, AuthenticationGateway authenticationGateway) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.authenticationGateway = authenticationGateway;
    }

    public void handle(String beneficiaryIban) {
        Iban validBeneficiaryIban = new Iban(beneficiaryIban);
        Customer currentCustomer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);
        Account account = currentCustomer.getAccount();
        beneficiaryRepository.findByAccountIdAndIban(account.getId(), validBeneficiaryIban.value())
                .orElseThrow(() -> {
                    throw new UnknownBeneficiaryException(validBeneficiaryIban.value());
                });
        beneficiaryRepository.delete(account.getId(), validBeneficiaryIban.value());
    }
}
