package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownBeneficiaryException;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryDateProvider;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryIdGenerator;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class TransferMoneyTest {
    @Test
    void shouldPerformAMoneyTransferBetweenTwoAccountInTheSameBank(){
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR3429051014050500014M02606";
        String senderAccountBIC = "AGRIFFRII89";
        String receiverAccountBIC = "AGRIFFRII89";
        String senderAccountId = "1";
        String receiverAccountId="2";
        String senderTransactionId = "13543A";
        String receiverTransactionId= "143E53245";
        String senderAccountFirstName = "Paul";
        String senderAccountLastName = "Duboit";
        String receiverAccountFirstName = "John";
        String receiverAccountLastName = "Smith";



        BigDecimal transactionAmount = new BigDecimal(5);
        Map<String,Account> dataSource = new HashMap<>();

        Account existingSenderAccount = new Account(senderAccountId,senderAccountIban,senderAccountBIC,new BigDecimal(105) , senderAccountFirstName, senderAccountLastName,new ArrayList<>(List.of(new MoneyTransferred("12345",Instant.ofEpochSecond(2534543253252L),new BigDecimal(105),receiverAccountIban,receiverAccountFirstName,receiverAccountLastName))), List.of(new Beneficiary("AE434",receiverAccountIban,receiverAccountBIC,receiverAccountFirstName,receiverAccountLastName)));
        dataSource.put(senderAccountIban,existingSenderAccount);
        dataSource.put(receiverAccountIban,new Account(receiverAccountId,receiverAccountIban,receiverAccountBIC,new BigDecimal(100),receiverAccountFirstName,receiverAccountLastName,null, List.of()));
        DateProvider dateProvider = new InMemoryDateProvider(1631000000000L);
        Instant currentInstant = dateProvider.now();
        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);
        IdGenerator idGenerator = new InMemoryIdGenerator(List.of(senderTransactionId,receiverTransactionId));
        TransferMoney transferMoney = new TransferMoney(accountRepository,dateProvider,idGenerator);

        transferMoney.handle(senderAccountIban,transactionAmount,receiverAccountIban);

        Account senderAccount = accountRepository.findByIban(senderAccountIban);
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban);
      assertThat(senderAccount.getBalance()).isEqualTo(new BigDecimal(100));
      assertThat(receiverAccount.getBalance()).isEqualTo(new BigDecimal(105));
      assertThat(senderAccount.getTransactions()).usingRecursiveComparison().isEqualTo(List.of(new MoneyTransferred("12345",Instant.ofEpochSecond(2534543253252L),new BigDecimal(105),receiverAccountIban,receiverAccountFirstName,receiverAccountLastName),new MoneyTransferred(senderTransactionId,currentInstant,new BigDecimal(-5),receiverAccountIban,receiverAccountFirstName,receiverAccountLastName)));
      assertThat(receiverAccount.getTransactions()).usingRecursiveComparison().isEqualTo(List.of(new MoneyTransferred(receiverTransactionId,currentInstant,new BigDecimal(5),senderAccountIban,senderAccountFirstName,senderAccountLastName)));
    }

    @Test
    void shouldThrowAnExceptionWhenTheReceiverAccountIsNotIsNotInTheBeneficiariesListOfTheSenderAccount(){
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR3429051014050500014M02606";
        String senderAccountBIC = "AGRIFFRII89";
        String receiverAccountBIC = "AGRIFFRII89";
        String senderAccountId = "1";
        String receiverAccountId="2";
        String senderTransactionId = "13543A";
        String receiverTransactionId= "143E53245";
        String senderAccountFirstName = "Paul";
        String senderAccountLastName = "Duboit";
        String receiverAccountFirstName = "John";
        String receiverAccountLastName = "Smith";



        BigDecimal transactionAmount = new BigDecimal(5);
        Map<String,Account> dataSource = new HashMap<>();

        Account existingSenderAccount = new Account(senderAccountId,senderAccountIban,senderAccountBIC,new BigDecimal(105) , senderAccountFirstName, senderAccountLastName,List.of(), List.of());
        dataSource.put(senderAccountIban,existingSenderAccount);
        dataSource.put(receiverAccountIban,new Account(receiverAccountId,receiverAccountIban,receiverAccountBIC,new BigDecimal(100),receiverAccountFirstName,receiverAccountLastName,null, List.of()));
        DateProvider dateProvider = new InMemoryDateProvider(1631000000000L);
        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);
        IdGenerator idGenerator = new InMemoryIdGenerator(List.of(senderTransactionId,receiverTransactionId));
        TransferMoney transferMoney = new TransferMoney(accountRepository,dateProvider,idGenerator);

        assertThatThrownBy(()-> transferMoney.handle(senderAccountIban,transactionAmount,receiverAccountIban)).isInstanceOf(UnknownBeneficiaryException.class)
                .hasMessage("Cannot find any account with the iban: " + receiverAccountIban + " in your beneficiaries list.");
    }

}