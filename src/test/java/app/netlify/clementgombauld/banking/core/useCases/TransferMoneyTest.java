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
        String senderAccountBIC = "AGRIFFRII89";
        String senderAccountId = "1";
        BigDecimal transactionAmount = new BigDecimal(5);
        Map<String,Account> dataSource = new HashMap<>();
        dataSource.put(senderAccountIban,new Account(senderAccountId,senderAccountIban,senderAccountBIC,new BigDecimal(105)));

        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);
        TransferMoney transferMoney = new TransferMoney(accountRepository);

        transferMoney.handle(senderAccountIban,senderAccountBIC,transactionAmount);

        Account senderAccount = accountRepository.findByIban(senderAccountIban);
      assertThat(senderAccount.getBalance()).isEqualTo(new BigDecimal(100));
    }

}