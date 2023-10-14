package app.netlify.clementgombauld.banking.account.usecases;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.domain.exceptions.AccountAlreadyOpenedException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.NoCurrentCustomerException;

import java.time.Instant;

public class OpenAccount {

    private final AccountRepository accountRepository;

    private final IbanGenerator ibanGenerator;

    private final IdGenerator idGenerator;

    private final AuthenticationGateway authenticationGateway;

    private final DateProvider dateProvider;

    public OpenAccount(AccountRepository accountRepository, IbanGenerator ibanGenerator, IdGenerator idGenerator, AuthenticationGateway authenticationGateway, DateProvider dateProvider) {
        this.accountRepository = accountRepository;
        this.ibanGenerator = ibanGenerator;
        this.idGenerator = idGenerator;
        this.authenticationGateway = authenticationGateway;
        this.dateProvider = dateProvider;
    }

    public void handle() {
        Customer currentCustomer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);
        String accountId = idGenerator.generate();
        Instant currentDate = dateProvider.now();
        Iban iban = ibanGenerator.generate();
        accountRepository.findByCustomerId(currentCustomer.getId())
                .ifPresent((c) -> {
                    throw new AccountAlreadyOpenedException(currentCustomer.getId());
                });
        accountRepository.insert(new Account.Builder()
                .withId(accountId)
                .withIban(iban)
                .withCustomer(currentCustomer)
                .withCreatedOn(currentDate)
                .build());

    }
}
