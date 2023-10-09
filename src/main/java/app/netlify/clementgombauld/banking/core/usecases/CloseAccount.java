package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.NoBicException;


public class CloseAccount {

    private final AccountRepository accountRepository;

    private final CustomerAccountFinder customerAccountFinder;

    public CloseAccount(AccountRepository accountRepository, AuthenticationGateway authenticationGateway) {
        this.accountRepository = accountRepository;
        this.customerAccountFinder = new CustomerAccountFinder(authenticationGateway, accountRepository);
    }

    public void handle(String externalAccountIban, String externalBic, String bic) {
        Account account = customerAccountFinder.findAccount();
        if (account.hasEmptyBalance()) {
            accountRepository.deleteById(account.getId());
            return;
        }
        new Iban(externalAccountIban);
        new Bic(externalBic);

    }
}
