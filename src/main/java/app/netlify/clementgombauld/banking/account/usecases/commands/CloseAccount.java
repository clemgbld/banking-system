package app.netlify.clementgombauld.banking.account.usecases.commands;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import app.netlify.clementgombauld.banking.common.domain.IdGenerator;

import java.time.Instant;


public class CloseAccount {

    private static final String CLOSE_ACCOUNT_REASON = "Close account withdrawal";

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

    public void handle(CloseAccountCommand command) {
        Account account = customerAccountFinder.findAccount();
        if (account.hasEmptyBalance()) {
            accountRepository.deleteById(account.getId());
            return;
        }
        Iban validExternalIban = new Iban(command.externalAccountIban());
        Bic validExternalBic = new Bic(command.externalBic());
        Bic validBankBic = new Bic(command.bic());
        Instant currentDate = dateProvider.now();
        String externalTransactionId = idGenerator.generate();
        String transactionId = idGenerator.generate();
        Customer currentCustomer = customerAccountFinder.currentCustomer();

        externalBankTransactionsGateway.transfer(new Transaction(externalTransactionId, currentDate, account.getBalance(), account.getIbanValue(), validBankBic.value(), currentCustomer.fullName(), CLOSE_ACCOUNT_REASON)
                , validExternalIban.value(),
                validExternalBic.value());

        transactionRepository.insert(account.getId(),
                new Transaction(transactionId, currentDate, account.negativeBalance(), validExternalIban.value(), validExternalBic.value(), command.accountName(), CLOSE_ACCOUNT_REASON));

        account.clearBalance();
        accountRepository.save(account);
        accountRepository.deleteById(account.getId());

    }
}
