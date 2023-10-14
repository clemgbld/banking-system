package app.netlify.clementgombauld.banking.account.domain;

import app.netlify.clementgombauld.banking.account.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.UnknownAccountWithCustomerId;

public class CustomerAccountFinder {
    private final AuthenticationGateway authenticationGateway;

    private final AccountRepository accountRepository;

    private Customer customer;

    public CustomerAccountFinder(AuthenticationGateway authenticationGateway, AccountRepository accountRepository) {
        this.authenticationGateway = authenticationGateway;
        this.accountRepository = accountRepository;
    }

    public Account findAccount() {
        customer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);
        return accountRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new UnknownAccountWithCustomerId(customer.getId()));
    }

    public Customer currentCustomer() {
        return customer;
    }


}
