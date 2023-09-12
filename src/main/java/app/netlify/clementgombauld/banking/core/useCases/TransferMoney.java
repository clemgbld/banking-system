package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.core.domain.DateProvider;
import app.netlify.clementgombauld.banking.core.domain.IdGenerator;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownAccountException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Supplier;

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
        Account senderAccount = accountRepository.findByIban(senderAccountIban)
                .orElseThrow(throwUnknownAccountException(senderAccountIban));
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban)
                .orElseThrow(throwUnknownAccountException(receiverAccountIban));
        String senderTransactionId = idGenerator.generate();
        String receiverTransactionId = idGenerator.generate();
        senderAccount.withdraw(senderTransactionId,creationDate,transactionAmount,receiverAccount.getIban());
        receiverAccount.credit(receiverTransactionId,creationDate,transactionAmount,senderAccount.getIban(),senderAccount.getFirstName(),senderAccount.getLastName());
        accountRepository.update(senderAccount,receiverAccount);
    }

    private Supplier<UnknownAccountException> throwUnknownAccountException(String iban){
        return ()-> {
          throw new UnknownAccountException(iban);
        };
    }
}
