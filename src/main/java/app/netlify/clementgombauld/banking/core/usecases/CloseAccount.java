package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.core.domain.Customer;
import app.netlify.clementgombauld.banking.core.domain.CustomerRepository;
import app.netlify.clementgombauld.banking.core.domain.ExternalBankTransactionsGateway;

public class CloseAccount {

    private final CustomerRepository customerRepository;
    private final AuthenticationGateway authenticationGateway;

    private final ExternalBankTransactionsGateway externalBankTransactionsGateway;

    public CloseAccount(CustomerRepository customerRepository, AuthenticationGateway authenticationGateway, ExternalBankTransactionsGateway externalBankTransactionsGateway) {
        this.customerRepository = customerRepository;
        this.authenticationGateway = authenticationGateway;
        this.externalBankTransactionsGateway = externalBankTransactionsGateway;
    }

    public void handle(String externalIban, String externalBic, String bic) {
        Customer currentCustomer = authenticationGateway.currentCustomer().get();
        customerRepository.delete(currentCustomer);
    }
}
