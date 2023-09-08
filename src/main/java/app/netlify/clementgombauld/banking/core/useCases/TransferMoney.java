package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.core.domain.DateProvider;
import app.netlify.clementgombauld.banking.core.domain.IdGenerator;

import java.math.BigDecimal;
import java.time.Instant;

public class TransferMoney {
    private final AccountRepository accountRepository;

    private final DateProvider dateProvider;

    private final IdGenerator idGenerator;

    public TransferMoney(AccountRepository accountRepository, DateProvider dateProvider, IdGenerator idGenerator) {
        this.accountRepository = accountRepository;
        this.dateProvider = dateProvider;
        this.idGenerator = idGenerator;
    }

    public void handle(String senderAccountIban, BigDecimal transactionAmount,String receiverAccountIban) {
        Instant creationDate = dateProvider.now();
        Account senderAccount = accountRepository.findByIban(senderAccountIban);
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban);
        String senderTransactionId = idGenerator.generate();
        String receiverTransactionId = idGenerator.generate();
        senderAccount.withdraw(senderTransactionId,creationDate,transactionAmount);
        receiverAccount.credit(receiverTransactionId,creationDate,transactionAmount);
        accountRepository.update(senderAccount,receiverAccount);
    }
}
