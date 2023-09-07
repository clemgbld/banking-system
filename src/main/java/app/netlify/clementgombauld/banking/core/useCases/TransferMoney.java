package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.core.domain.DateProvider;
import app.netlify.clementgombauld.banking.core.domain.IdGenerator;

import java.math.BigDecimal;

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
        Account senderAccount = accountRepository.findByIban(senderAccountIban);
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban);
        senderAccount.withdraw(transactionAmount);
        receiverAccount.credit(transactionAmount);
        accountRepository.update(senderAccount,receiverAccount);
    }
}
