package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;

import java.time.Instant;


public class CloseAccount {

    private final AccountRepository accountRepository;

    private final CustomerAccountFinder customerAccountFinder;

    private final ExternalBankTransactionsGateway externalBankTransactionsGateway;

    private final DateProvider dateProvider;

    private final IdGenerator idGenerator;

    public CloseAccount(AccountRepository accountRepository, AuthenticationGateway authenticationGateway, ExternalBankTransactionsGateway externalBankTransactionsGateway, DateProvider dateProvider, IdGenerator idGenerator) {
        this.accountRepository = accountRepository;
        this.externalBankTransactionsGateway = externalBankTransactionsGateway;
        this.dateProvider = dateProvider;
        this.idGenerator = idGenerator;
        this.customerAccountFinder = new CustomerAccountFinder(authenticationGateway, accountRepository);
    }

    public void handle(String externalAccountIban, String externalBic, String bic, String accountName) {
        Account account = customerAccountFinder.findAccount();
        if (account.hasEmptyBalance()) {
            accountRepository.deleteById(account.getId());
            return;
        }
        Iban validExternalIban = new Iban(externalAccountIban);
        Bic validExternalBic = new Bic(externalBic);
        Bic validBankBic = new Bic(bic);
        Instant currentDate = dateProvider.now();
        String externalTransactionId = idGenerator.generate();
        Customer currentCustomer = customerAccountFinder.currentCustomer();

        externalBankTransactionsGateway.transfer(new Transaction(externalTransactionId, currentDate, account.getBalance(), account.getIban(), validBankBic.value(), currentCustomer.fullName())
                , validExternalIban.value(),
                validExternalBic.value());
        
        account.clearBalance();
        accountRepository.update(account);
        accountRepository.deleteById(account.getId());

    }
}
