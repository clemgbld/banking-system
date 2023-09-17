package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.InsufficientBalanceException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownAccountException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownBeneficiaryException;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryDateProvider;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryExtraBankTransactionsGateway;
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
    void shouldPerformAMoneyTransferBetweenTwoAccountsInTheSameBank(){
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

        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(senderAccountIban)
                .withBic(senderAccountBIC)
                .withBalance(new BigDecimal(105))
                .withFirstName(senderAccountFirstName)
                .withLastName(senderAccountLastName)
                .withTransactions(new ArrayList<>(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban, receiverAccountFirstName + " " + receiverAccountLastName))))
                .withBeneficiaries(List.of(new Beneficiary("AE434", receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName)))
                .build();

        dataSource.put(senderAccountIban,existingSenderAccount);
        dataSource.put(receiverAccountIban,new Account.Builder()
                .withId(receiverAccountId)
                .withIban(receiverAccountIban)
                .withBic(receiverAccountBIC)
                .withBalance(new BigDecimal(100))
                .withFirstName(receiverAccountFirstName)
                .withLastName(receiverAccountLastName)
                .withTransactions(null)
                .withBeneficiaries(List.of())
                .build());
        DateProvider dateProvider = new InMemoryDateProvider(1631000000000L);
        Instant currentInstant = dateProvider.now();
        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);
        IdGenerator idGenerator = new InMemoryIdGenerator(List.of(senderTransactionId,receiverTransactionId));
        ExtraBankTransactionsGateway extraBankTransactionsGateway = new InMemoryExtraBankTransactionsGateway(List.of());
        TransferMoney transferMoney = new TransferMoney(accountRepository,dateProvider,idGenerator, extraBankTransactionsGateway);

        transferMoney.handle(senderAccountIban,transactionAmount,receiverAccountIban);

        Account senderAccount = accountRepository.findByIban(senderAccountIban).orElseThrow(RuntimeException::new);
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);
      assertThat(senderAccount.getBalance()).isEqualTo(new BigDecimal(100));
      assertThat(receiverAccount.getBalance()).isEqualTo(new BigDecimal(105));
      assertThat(senderAccount.getTransactions()).usingRecursiveComparison().isEqualTo(List.of(new MoneyTransferred("12345",Instant.ofEpochSecond(2534543253252L),new BigDecimal(105),receiverAccountIban,receiverAccountFirstName + " " + receiverAccountLastName),new MoneyTransferred(senderTransactionId,currentInstant,new BigDecimal(-5),receiverAccountIban,receiverAccountFirstName + " " + receiverAccountLastName)));
      assertThat(receiverAccount.getTransactions()).usingRecursiveComparison().isEqualTo(List.of(new MoneyTransferred(receiverTransactionId,currentInstant,new BigDecimal(5),senderAccountIban,senderAccountFirstName + " " +senderAccountLastName)));
    }

    @Test
    void shouldPerformAMoneyTransferBetweenTwoAccountsThatAreNotInTheSameBank(){
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR3429051014050500014M02606";
        String senderAccountBIC = "AGRIFFRII89";
        String receiverAccountBIC = "AGRIFBCII89";
        String senderAccountId = "1";
        String senderTransactionId = "13543A";
        String receiverTransactionId= "143E53245";
        String senderAccountFirstName = "Paul";
        String senderAccountLastName = "Duboit";
        String receiverAccountFirstName = "John";
        String receiverAccountLastName = "Smith";



        BigDecimal transactionAmount = new BigDecimal(5);
        Map<String,Account> dataSource = new HashMap<>();

        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(senderAccountIban)
                .withBic(senderAccountBIC)
                .withBalance(new BigDecimal(105))
                .withFirstName(senderAccountFirstName)
                .withLastName(senderAccountLastName)
                .withTransactions(new ArrayList<>(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban, receiverAccountFirstName + " " + receiverAccountLastName))))
                .withBeneficiaries(List.of(new Beneficiary("AE434", receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName)))
                .build();

        dataSource.put(senderAccountIban,existingSenderAccount);
        DateProvider dateProvider = new InMemoryDateProvider(1631000000000L);
        Instant currentInstant = dateProvider.now();
        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);
        List<MoneyTransferred> extraBankTransactions = new ArrayList<>();
        ExtraBankTransactionsGateway extraBankTransactionsGateway = new InMemoryExtraBankTransactionsGateway(extraBankTransactions);
        IdGenerator idGenerator = new InMemoryIdGenerator(List.of(senderTransactionId,receiverTransactionId));
        TransferMoney transferMoney = new TransferMoney(accountRepository,dateProvider,idGenerator,extraBankTransactionsGateway);


         transferMoney.handle(senderAccountIban,transactionAmount,receiverAccountIban);

        Account senderAccount = accountRepository.findByIban(senderAccountIban).orElseThrow(RuntimeException::new);
        assertThat(senderAccount.getBalance()).isEqualTo(new BigDecimal(100));
        assertThat(senderAccount.getTransactions()).usingRecursiveComparison().isEqualTo(List.of(new MoneyTransferred("12345",Instant.ofEpochSecond(2534543253252L),new BigDecimal(105),receiverAccountIban,receiverAccountFirstName + " " + receiverAccountLastName),new MoneyTransferred(senderTransactionId,currentInstant,new BigDecimal(-5),receiverAccountIban,receiverAccountFirstName + " " + receiverAccountLastName)));
        assertThat(extraBankTransactions).usingRecursiveComparison().isEqualTo(List.of(new MoneyTransferred(receiverTransactionId,currentInstant,new BigDecimal(5),senderAccountIban,senderAccountFirstName + " " + senderAccountLastName)));
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

          Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(senderAccountIban)
                .withBic(senderAccountBIC)
                .withBalance(new BigDecimal(105))
                .withFirstName(senderAccountFirstName)
                .withLastName(senderAccountLastName)
                .withTransactions(List.of())
                .withBeneficiaries(List.of())
                .build();
        dataSource.put(senderAccountIban,existingSenderAccount);

        dataSource.put(receiverAccountIban,new Account.Builder()
                .withId(receiverAccountId)
                .withIban(receiverAccountIban)
                .withBic(receiverAccountBIC)
                .withBalance(new BigDecimal(100))
                .withFirstName(receiverAccountFirstName)
                .withLastName(receiverAccountLastName)
                .withTransactions(null)
                .withBeneficiaries(List.of())
                .build());

        DateProvider dateProvider = new InMemoryDateProvider(1631000000000L);
        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);
        IdGenerator idGenerator = new InMemoryIdGenerator(List.of(senderTransactionId,receiverTransactionId));
        ExtraBankTransactionsGateway extraBankTransactionsGateway = new InMemoryExtraBankTransactionsGateway(List.of());
        TransferMoney transferMoney = new TransferMoney(accountRepository,dateProvider,idGenerator, extraBankTransactionsGateway);

        assertThatThrownBy(()-> transferMoney.handle(senderAccountIban,transactionAmount,receiverAccountIban)).isInstanceOf(UnknownBeneficiaryException.class)
                .hasMessage("Cannot find any account with the iban: " + receiverAccountIban + " in your beneficiaries list.");
    }

   @Test
    void shouldThrowAnExceptionWhenTheSenderAccountBalanceIsInsufficient(){
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

       BigDecimal transactionAmount = new BigDecimal(1000);
       Map<String,Account> dataSource = new HashMap<>();

       Account existingSenderAccount = new Account.Builder()
               .withId(senderAccountId)
               .withIban(senderAccountIban)
               .withBic(senderAccountBIC)
               .withBalance(new BigDecimal(105))
               .withFirstName(senderAccountFirstName)
               .withLastName(senderAccountLastName)
               .withTransactions(new ArrayList<>(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban, receiverAccountFirstName + " " + receiverAccountLastName))))
               .withBeneficiaries(List.of(new Beneficiary("AE434", receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName)))
               .build();
       dataSource.put(senderAccountIban,existingSenderAccount);
       dataSource.put(receiverAccountIban,new Account.Builder()
               .withId(receiverAccountId)
               .withIban(receiverAccountIban)
               .withBic(receiverAccountBIC)
               .withBalance(new BigDecimal(100))
               .withFirstName(receiverAccountFirstName)
               .withLastName(receiverAccountLastName)
               .withTransactions(null)
               .withBeneficiaries(List.of())
               .build());
       DateProvider dateProvider = new InMemoryDateProvider(1631000000000L);
       AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);
       IdGenerator idGenerator = new InMemoryIdGenerator(List.of(senderTransactionId,receiverTransactionId));
       ExtraBankTransactionsGateway extraBankTransactionsGateway = new InMemoryExtraBankTransactionsGateway(List.of());
       TransferMoney transferMoney = new TransferMoney(accountRepository,dateProvider,idGenerator, extraBankTransactionsGateway);

       assertThatThrownBy(()-> transferMoney.handle(senderAccountIban,transactionAmount,receiverAccountIban)).isInstanceOf(InsufficientBalanceException.class);
   }

   @Test
    void shouldThrowAnExceptionWhenTheSenderAccountDoesNotExists(){
       String senderAccountIban = "FR1420041010050500013M02606";
       String receiverAccountIban = "FR3429051014050500014M02606";
       String senderTransactionId = "13543A";
       String receiverTransactionId= "143E53245";

       BigDecimal transactionAmount = new BigDecimal(5);
       Map<String,Account> dataSource = new HashMap<>();

       DateProvider dateProvider = new InMemoryDateProvider(1631000000000L);
       AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);
       IdGenerator idGenerator = new InMemoryIdGenerator(List.of(senderTransactionId,receiverTransactionId));
       ExtraBankTransactionsGateway extraBankTransactionsGateway = new InMemoryExtraBankTransactionsGateway(List.of());
       TransferMoney transferMoney = new TransferMoney(accountRepository,dateProvider,idGenerator, extraBankTransactionsGateway);

       assertThatThrownBy(()-> transferMoney.handle(senderAccountIban,transactionAmount,receiverAccountIban)).isInstanceOf(UnknownAccountException.class)
               .hasMessage("There is no account with the iban: " + senderAccountIban);
   }

    @Test
    void shouldThrowAnExceptionWhenTheReceiverAccountDoesNotExists(){
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR3429051014050500014M02606";
        String senderAccountBIC = "AGRIFFRII89";
        String receiverAccountBIC = "AGRIFFRII89";
        String senderAccountId = "1";
        String senderTransactionId = "13543A";
        String receiverTransactionId= "143E53245";
        String senderAccountFirstName = "Paul";
        String senderAccountLastName = "Duboit";
        String receiverAccountFirstName = "John";
        String receiverAccountLastName = "Smith";

        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(senderAccountIban)
                .withBic(senderAccountBIC)
                .withBalance(new BigDecimal(105))
                .withFirstName(senderAccountFirstName)
                .withLastName(senderAccountLastName)
                .withTransactions(new ArrayList<>(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban, receiverAccountFirstName + " " + receiverAccountLastName))))
                .withBeneficiaries(List.of(new Beneficiary("AE434", receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName)))
                .build();

        BigDecimal transactionAmount = new BigDecimal(5);
        Map<String,Account> dataSource = new HashMap<>();
        dataSource.put(senderAccountIban,existingSenderAccount);

        DateProvider dateProvider = new InMemoryDateProvider(1631000000000L);
        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);
        IdGenerator idGenerator = new InMemoryIdGenerator(List.of(senderTransactionId,receiverTransactionId));
        ExtraBankTransactionsGateway extraBankTransactionsGateway = new InMemoryExtraBankTransactionsGateway(List.of());
        TransferMoney transferMoney = new TransferMoney(accountRepository,dateProvider,idGenerator, extraBankTransactionsGateway);

        assertThatThrownBy(()-> transferMoney.handle(senderAccountIban,transactionAmount,receiverAccountIban)).isInstanceOf(UnknownAccountException.class)
                .hasMessage("There is no account with the iban: " + receiverAccountIban);
    }


}