package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.NoCurrentCustomerException;

public class OpenAccount {

    private final CustomerRepository customerRepository;

    private final IbanGenerator ibanGenerator;

    private final IdGenerator idGenerator;

    private final AuthenticationGateway authenticationGateway;

    public OpenAccount(CustomerRepository customerRepository, IbanGenerator ibanGenerator, IdGenerator idGenerator, AuthenticationGateway authenticationGateway) {
        this.customerRepository = customerRepository;
        this.ibanGenerator = ibanGenerator;
        this.idGenerator = idGenerator;
        this.authenticationGateway = authenticationGateway;
    }

    public void handle() {
        Customer customer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);
        String accountId = idGenerator.generate();
        Iban iban = ibanGenerator.generate();

        customer.openAccount(new Account.Builder()
                .withId(accountId)
                .withIban(iban)
                .build());

        customerRepository.update(customer);

    }
}
