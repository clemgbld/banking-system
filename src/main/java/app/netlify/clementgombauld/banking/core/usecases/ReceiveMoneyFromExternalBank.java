package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.core.domain.DateProvider;
import app.netlify.clementgombauld.banking.core.domain.IdGenerator;

import java.math.BigDecimal;
import java.time.Instant;

public class ReceiveMoneyFromExternalBank {

    private final AccountRepository accountRepository;

    private final IdGenerator idGenerator;

    private final DateProvider dateProvider;

    public ReceiveMoneyFromExternalBank(AccountRepository accountRepository, IdGenerator idGenerator, DateProvider dateProvider) {

        this.accountRepository = accountRepository;
        this.idGenerator = idGenerator;
        this.dateProvider = dateProvider;
    }

    public void handle(String receiverAccountIban, String senderAccountIban, String senderAccountBic, String senderAccountName, BigDecimal transactionAmount) {
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);
        String transactionId = idGenerator.generate();
        Instant currentDate = dateProvider.now();
        receiverAccount.deposit(transactionId, currentDate, transactionAmount, senderAccountIban, senderAccountBic, senderAccountName);
        accountRepository.update(receiverAccount);
    }
}
