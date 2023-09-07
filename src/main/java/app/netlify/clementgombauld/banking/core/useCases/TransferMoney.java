package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;

import java.math.BigDecimal;

public class TransferMoney {
    private final AccountRepository accountRepository;

    public TransferMoney(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void handle(String senderAccountIban, BigDecimal transactionAmount,String receiverAccountIban) {
        Account senderAccount = accountRepository.findByIban(senderAccountIban);
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban);
        senderAccount.withdraw(transactionAmount);
        receiverAccount.credit(transactionAmount);
        accountRepository.update(senderAccount,receiverAccount);
    }
}
