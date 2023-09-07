package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAccountRepository;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;


class TransferMoneyTest {
    @Test
    void shouldPerformAMoneyTransferBetweenTwoAccountInTheSameBank(){
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR3429051014050500014M02606";
        String senderAccountBIC = "AGRIFFRII89";
        String receiverAccountBIC = "AGRIFFRII89";
        String senderAccountId = "1";
        String receiverAccountId="2";
        BigDecimal transactionAmount = new BigDecimal(5);
        Map<String,Account> dataSource = new HashMap<>();
        dataSource.put(senderAccountIban,new Account(senderAccountId,senderAccountIban,senderAccountBIC,new BigDecimal(105)));
        dataSource.put(receiverAccountIban,new Account(receiverAccountId,receiverAccountIban,receiverAccountBIC,new BigDecimal(100)));

        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);
        TransferMoney transferMoney = new TransferMoney(accountRepository);

        transferMoney.handle(senderAccountIban,transactionAmount,receiverAccountIban);

        Account senderAccount = accountRepository.findByIban(senderAccountIban);
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban);
      assertThat(senderAccount.getBalance()).isEqualTo(new BigDecimal(100));
      assertThat(receiverAccount.getBalance()).isEqualTo(new BigDecimal(105));
    }

}