package app.netlify.clementgombauld.banking.core.usecases;


import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownAccountWithIbanException;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Supplier;

public class TransferMoney {
    private final AccountRepository accountRepository;

    private final DateProvider dateProvider;

    private final IdGenerator idGenerator;

    private final ExtraBankTransactionsGateway extraBankTransactionsGateway;

    private final AuthenticationGateway authenticationGateway;

    public TransferMoney(AccountRepository accountRepository, DateProvider dateProvider, IdGenerator idGenerator, ExtraBankTransactionsGateway extraBankTransactionsGateway, AuthenticationGateway authenticationGateway) {
        this.accountRepository = accountRepository;
        this.dateProvider = dateProvider;
        this.idGenerator = idGenerator;
        this.extraBankTransactionsGateway = extraBankTransactionsGateway;
        this.authenticationGateway = authenticationGateway;
    }

    public void handle(BigDecimal transactionAmount, String receiverAccountIban, String bic) {
        Bic bankBic = new Bic(bic);
        Instant creationDate = dateProvider.now();
        Customer currentCustomer = authenticationGateway.currentCustomer()
                .orElseThrow(NoCurrentCustomerException::new);
        Account senderAccount = currentCustomer.getAccount();
        String senderTransactionId = idGenerator.generate();
        String receiverTransactionId = idGenerator.generate();
        senderAccount.withdraw(senderTransactionId, creationDate, transactionAmount, receiverAccountIban);
        Beneficiary beneficiary = senderAccount.findBeneficiaryByIbanOrThrow(receiverAccountIban);
        if (beneficiary.isInDifferentBank(bankBic.value())) {
            accountRepository.update(senderAccount);
            MoneyTransferred transaction = new MoneyTransferred(receiverTransactionId, creationDate, transactionAmount, senderAccount.getIban(), bankBic.value(), currentCustomer.fullName());
            extraBankTransactionsGateway.transfer(transaction, beneficiary.getIban(), beneficiary.getBic());
            return;
        }
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban)
                .orElseThrow(throwUnknownAccountException(receiverAccountIban));

        receiverAccount.deposit(receiverTransactionId, creationDate, transactionAmount, senderAccount.getIban(), bankBic.value(), currentCustomer.fullName());
        accountRepository.update(senderAccount, receiverAccount);
    }

    private Supplier<UnknownAccountWithIbanException> throwUnknownAccountException(String iban) {
        return () -> {
            throw new UnknownAccountWithIbanException(iban);
        };
    }
}
