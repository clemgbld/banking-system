package app.netlify.clementgombauld.banking.core.usecases;


import app.netlify.clementgombauld.banking.core.domain.*;
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

    public TransferMoney(AccountRepository accountRepository, DateProvider dateProvider, IdGenerator idGenerator, ExtraBankTransactionsGateway extraBankTransactionsGateway,AuthenticationGateway authenticationGateway) {
        this.accountRepository = accountRepository;
        this.dateProvider = dateProvider;
        this.idGenerator = idGenerator;
        this.extraBankTransactionsGateway = extraBankTransactionsGateway;
        this.authenticationGateway = authenticationGateway;
    }

    public void handle(BigDecimal transactionAmount,String receiverAccountIban) {
        Instant creationDate = dateProvider.now();
        Customer currentCustomer = authenticationGateway.currentCustomer()
                .orElseThrow(RuntimeException::new);
        Account senderAccount = currentCustomer.getAccount();
        String senderTransactionId = idGenerator.generate();
        String receiverTransactionId = idGenerator.generate();
        senderAccount.withdraw(senderTransactionId,creationDate,transactionAmount,receiverAccountIban);
        if(senderAccount.isInDifferentBank(receiverAccountIban)){
             accountRepository.update(senderAccount);
             extraBankTransactionsGateway.transfer(new MoneyTransferred(receiverTransactionId,creationDate,transactionAmount,senderAccount.getIban(), senderAccount.getBic(),senderAccount.getFullName()));
             return;
        }
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban)
                .orElseThrow(throwUnknownAccountException(receiverAccountIban));

        receiverAccount.deposit(receiverTransactionId,creationDate,transactionAmount,senderAccount.getIban(),senderAccount.getBic(),senderAccount.getFirstName(),senderAccount.getLastName());
        accountRepository.update(senderAccount,receiverAccount);
    }

    private Supplier<UnknownAccountWithIbanException> throwUnknownAccountException(String iban){
        return ()-> {
          throw new UnknownAccountWithIbanException(iban);
        };
    }
}
