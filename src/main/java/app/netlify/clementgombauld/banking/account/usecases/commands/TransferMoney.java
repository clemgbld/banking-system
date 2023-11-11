package app.netlify.clementgombauld.banking.account.usecases.commands;


import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.domain.exceptions.UnknownAccountWithIbanException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.UnknownBeneficiaryException;
import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import app.netlify.clementgombauld.banking.common.domain.IdGenerator;


import java.time.Instant;
import java.util.function.Supplier;

public class TransferMoney {
    private final AccountRepository accountRepository;

    private final BeneficiaryRepository beneficiaryRepository;

    private final TransactionRepository transactionRepository;

    private final DateProvider dateProvider;

    private final IdGenerator idGenerator;

    private final ExternalBankTransactionsGateway externalBankTransactionsGateway;

    private final CustomerAccountFinder customerAccountFinder;

    public TransferMoney(AccountRepository accountRepository, BeneficiaryRepository beneficiaryRepository, TransactionRepository transactionRepository, DateProvider dateProvider, IdGenerator idGenerator, ExternalBankTransactionsGateway externalBankTransactionsGateway, AuthenticationGateway authenticationGateway) {
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.transactionRepository = transactionRepository;
        this.dateProvider = dateProvider;
        this.idGenerator = idGenerator;
        this.externalBankTransactionsGateway = externalBankTransactionsGateway;
        this.customerAccountFinder = new CustomerAccountFinder(authenticationGateway, accountRepository);
    }

    public void handle(TransferMoneyCommand command) {
        Bic bankBic = new Bic(command.bic());
        Instant creationDate = dateProvider.now();
        Account senderAccount = customerAccountFinder.findAccount();
        Customer currentCustomer = customerAccountFinder.currentCustomer();
        String senderTransactionId = idGenerator.generate();
        String receiverTransactionId = idGenerator.generate();
        senderAccount.withdraw(command.transactionAmount());
        Beneficiary beneficiary = beneficiaryRepository.findByAccountIdAndIban(senderAccount.getId(), command.receiverAccountIdentifier())
                .orElseThrow(() -> new UnknownBeneficiaryException(command.receiverAccountIdentifier()));
        transactionRepository.insert(senderAccount.getId(), senderAccount.recordWithdrawalTransaction(senderTransactionId, creationDate, command.transactionAmount(), command.receiverAccountIdentifier(), new Bic(beneficiary.getBic()), beneficiary.getName(), command.reason()));
        if (beneficiary.isInDifferentBank(bankBic)) {
            accountRepository.save(senderAccount);
            Transaction transaction = new Transaction(receiverTransactionId, creationDate, command.transactionAmount(), senderAccount.getIbanValue(), bankBic.value(), currentCustomer.fullName(), command.reason());
            externalBankTransactionsGateway.transfer(transaction, beneficiary.getIban(), beneficiary.getBic());
            return;
        }
        Account receiverAccount = accountRepository.findByIban(command.receiverAccountIdentifier())
                .orElseThrow(throwUnknownAccountException(command.receiverAccountIdentifier()));

        receiverAccount.deposit(command.transactionAmount());
        accountRepository.save(senderAccount, receiverAccount);
        transactionRepository.insert(receiverAccount.getId(), receiverAccount.recordDepositTransaction(receiverTransactionId, creationDate, command.transactionAmount(), senderAccount.getIbanValue(), bankBic, currentCustomer.fullName(), command.reason()));
    }

    private Supplier<UnknownAccountWithIbanException> throwUnknownAccountException(String iban) {
        return () -> {
            throw new UnknownAccountWithIbanException(iban);
        };
    }
}
