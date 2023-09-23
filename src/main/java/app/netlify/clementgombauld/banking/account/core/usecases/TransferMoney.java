package app.netlify.clementgombauld.banking.account.core.usecases;

import app.netlify.clementgombauld.banking.account.core.domain.*;
import app.netlify.clementgombauld.banking.account.core.domain.exceptions.UnknownAccountWithIbanException;
import app.netlify.clementgombauld.banking.core.domain.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Supplier;

public class TransferMoney {
    private final AccountRepository accountRepository;

    private final DateProvider dateProvider;

    private final IdGenerator idGenerator;

    private final ExtraBankTransactionsGateway extraBankTransactionsGateway;

    public TransferMoney(AccountRepository accountRepository, DateProvider dateProvider, IdGenerator idGenerator, ExtraBankTransactionsGateway extraBankTransactionsGateway) {
        this.accountRepository = accountRepository;
        this.dateProvider = dateProvider;
        this.idGenerator = idGenerator;
        this.extraBankTransactionsGateway = extraBankTransactionsGateway;
    }

    public void handle(String senderAccountIban, BigDecimal transactionAmount,String receiverAccountIban) {
        Instant creationDate = dateProvider.now();
        Account senderAccount = accountRepository.findByIban(senderAccountIban)
                .orElseThrow(throwUnknownAccountException(senderAccountIban));
        String senderTransactionId = idGenerator.generate();
        String receiverTransactionId = idGenerator.generate();
        senderAccount.withdraw(senderTransactionId,creationDate,transactionAmount,receiverAccountIban);
        if(senderAccount.isInDifferentBank(receiverAccountIban)){
             accountRepository.update(senderAccount);
             extraBankTransactionsGateway.transfer(new MoneyTransferred(receiverTransactionId,creationDate,transactionAmount,senderAccountIban, senderAccount.getBic(),senderAccount.getFullName()));
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
