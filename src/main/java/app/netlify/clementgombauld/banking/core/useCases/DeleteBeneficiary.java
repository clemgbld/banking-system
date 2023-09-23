package app.netlify.clementgombauld.banking.core.useCases;


import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.core.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.core.domain.Customer;


public class DeleteBeneficiary {
    private final AccountRepository accountRepository;
    private final AuthenticationGateway authenticationGateway;
    public DeleteBeneficiary(AccountRepository accountRepository, AuthenticationGateway authenticationGateway) {
        this.accountRepository = accountRepository;
        this.authenticationGateway = authenticationGateway;
    }

    public void handle( String beneficiaryIban) {
        Customer currentCustomer = authenticationGateway.currentCustomer()
                .orElseThrow(RuntimeException::new);
        Account account = currentCustomer.getAccount();
        account.deleteBeneficiary(beneficiaryIban);
        accountRepository.update(account);
    }
}
