package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.core.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.core.domain.Customer;

public class CloseAccount {

    private final AccountRepository accountRepository;

    private final AuthenticationGateway authenticationGateway;

    public CloseAccount(AccountRepository accountRepository, AuthenticationGateway authenticationGateway) {
        this.accountRepository = accountRepository;
        this.authenticationGateway = authenticationGateway;
    }

    public void handle(String externalAccountIban, String externalBic, String bic) {
        Customer currentCustomer = authenticationGateway.currentCustomer().orElseThrow();
        accountRepository.deleteByCustomerId(currentCustomer.getId());
    }
}
