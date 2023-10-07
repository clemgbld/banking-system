package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.AccountAlreadyOpenedException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.NoCurrentCustomerException;

public class OpenAccount {

    private final AccountRepository accountRepository;

    private final IbanGenerator ibanGenerator;

    private final IdGenerator idGenerator;

    private final AuthenticationGateway authenticationGateway;

    public OpenAccount(AccountRepository accountRepository, IbanGenerator ibanGenerator, IdGenerator idGenerator, AuthenticationGateway authenticationGateway) {
        this.accountRepository = accountRepository;
        this.ibanGenerator = ibanGenerator;
        this.idGenerator = idGenerator;
        this.authenticationGateway = authenticationGateway;
    }

    public void handle() {
        Customer currentCustomer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);
        String accountId = idGenerator.generate();
        Iban iban = ibanGenerator.generate();
        accountRepository.findByCustomerId(currentCustomer.getId())
                .ifPresent((c) -> {
                    throw new AccountAlreadyOpenedException(currentCustomer.getId());
                });
        accountRepository.insert(new Account.Builder()
                .withId(accountId)
                .withIban(iban)
                .withCustomer(currentCustomer)
                .build());

    }
}
