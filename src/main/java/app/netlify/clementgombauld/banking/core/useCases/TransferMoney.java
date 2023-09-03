package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;

import java.math.BigDecimal;

public class TransferMoney {
    private final AccountRepository accountRepository;

    public TransferMoney(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void handle(String senderAccountIban, String senderAccountBIC, BigDecimal transactionAmount) {
        Account senderAccount = accountRepository.findByIban(senderAccountIban);
        senderAccount.withdraw(transactionAmount);
        accountRepository.update(senderAccount);
    }
}
