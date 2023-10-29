package app.netlify.clementgombauld.banking.account.usecases;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.usecases.commands.CloseAccountCommand;
import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import app.netlify.clementgombauld.banking.common.domain.IdGenerator;

import java.time.Instant;


public class CloseAccount {

    private final AccountRepository accountRepository;

    private final CustomerAccountFinder customerAccountFinder;

    private final ExternalBankTransactionsGateway externalBankTransactionsGateway;

    private final DateProvider dateProvider;

    private final IdGenerator idGenerator;

    private final TransactionRepository transactionRepository;

    public CloseAccount(AccountRepository accountRepository, AuthenticationGateway authenticationGateway, ExternalBankTransactionsGateway externalBankTransactionsGateway, DateProvider dateProvider, IdGenerator idGenerator, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.externalBankTransactionsGateway = externalBankTransactionsGateway;
        this.dateProvider = dateProvider;
        this.idGenerator = idGenerator;
        this.transactionRepository = transactionRepository;
        this.customerAccountFinder = new CustomerAccountFinder(authenticationGateway, accountRepository);
    }

    public void handle(CloseAccountCommand closeAccountCommand) {
        Account account = customerAccountFinder.findAccount();
        if (account.hasEmptyBalance()) {
            accountRepository.deleteById(account.getId());
            return;
        }
        Iban validExternalIban = new Iban(closeAccountCommand.externalAccountIban());
        Bic validExternalBic = new Bic(closeAccountCommand.externalBic());
        Bic validBankBic = new Bic(closeAccountCommand.bic());
        Instant currentDate = dateProvider.now();
        String externalTransactionId = idGenerator.generate();
        String transactionId = idGenerator.generate();
        Customer currentCustomer = customerAccountFinder.currentCustomer();

        externalBankTransactionsGateway.transfer(new Transaction(externalTransactionId, currentDate, account.getBalance(), account.getIban(), validBankBic.value(), currentCustomer.fullName())
                , validExternalIban.value(),
                validExternalBic.value());

        transactionRepository.insert(account.getId(),
                new Transaction(transactionId, currentDate, account.negativeBalance(), validExternalIban.value(), validExternalBic.value(), closeAccountCommand.accountName()));

        account.clearBalance();
        accountRepository.update(account);
        accountRepository.deleteById(account.getId());

    }
}
