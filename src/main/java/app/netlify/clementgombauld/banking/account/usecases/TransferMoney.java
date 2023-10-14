package app.netlify.clementgombauld.banking.account.usecases;


import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.domain.exceptions.UnknownAccountWithIbanException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.UnknownBeneficiaryException;


import java.math.BigDecimal;
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

    public void handle(BigDecimal transactionAmount, String receiverAccountIdentifier, String bic) {
        Bic bankBic = new Bic(bic);
        Instant creationDate = dateProvider.now();
        Account senderAccount = customerAccountFinder.findAccount();
        Customer currentCustomer = customerAccountFinder.currentCustomer();
        String senderTransactionId = idGenerator.generate();
        String receiverTransactionId = idGenerator.generate();
        senderAccount.withdraw(transactionAmount);
        Beneficiary beneficiary = beneficiaryRepository.findByAccountIdAndIban(senderAccount.getId(), receiverAccountIdentifier)
                .orElseThrow(() -> new UnknownBeneficiaryException(receiverAccountIdentifier));
        transactionRepository.insert(senderAccount.getId(), new Transaction(senderTransactionId, creationDate, transactionAmount.negate(), receiverAccountIdentifier, beneficiary.getBic(), beneficiary.getName()));

        if (beneficiary.isInDifferentBank(bankBic)) {
            accountRepository.update(senderAccount);
            Transaction transaction = new Transaction(receiverTransactionId, creationDate, transactionAmount, senderAccount.getIban(), bankBic.value(), currentCustomer.fullName());
            externalBankTransactionsGateway.transfer(transaction, beneficiary.getIban(), beneficiary.getBic());
            return;
        }
        Account receiverAccount = accountRepository.findByIban(receiverAccountIdentifier)
                .orElseThrow(throwUnknownAccountException(receiverAccountIdentifier));

        receiverAccount.deposit(transactionAmount);
        accountRepository.update(senderAccount, receiverAccount);
        transactionRepository.insert(receiverAccount.getId(), new Transaction(receiverTransactionId, creationDate, transactionAmount, senderAccount.getIban(), bankBic.value(), currentCustomer.fullName()));
    }

    private Supplier<UnknownAccountWithIbanException> throwUnknownAccountException(String iban) {
        return () -> {
            throw new UnknownAccountWithIbanException(iban);
        };
    }
}
