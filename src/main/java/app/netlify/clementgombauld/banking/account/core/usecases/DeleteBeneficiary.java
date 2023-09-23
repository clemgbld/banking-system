package app.netlify.clementgombauld.banking.account.core.usecases;


import app.netlify.clementgombauld.banking.account.core.domain.Account;
import app.netlify.clementgombauld.banking.account.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.account.core.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.core.domain.Customer;
import app.netlify.clementgombauld.banking.account.core.domain.exceptions.NoCurrentCustomerException;


public class DeleteBeneficiary {
    private final AccountRepository accountRepository;
    private final AuthenticationGateway authenticationGateway;
    public DeleteBeneficiary(AccountRepository accountRepository, AuthenticationGateway authenticationGateway) {
        this.accountRepository = accountRepository;
        this.authenticationGateway = authenticationGateway;
    }

    public void handle( String beneficiaryIban) {
        Customer currentCustomer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);
        Account account = currentCustomer.getAccount();
        account.deleteBeneficiary(beneficiaryIban);
        accountRepository.update(account);
    }
}
