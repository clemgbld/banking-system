package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.InsufficientBalanceException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownAccountWithIbanException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownBeneficiaryException;
import app.netlify.clementgombauld.banking.infra.inMemory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;



import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class TransferMoneyTest {

    private AuthenticationGateway authenticationGateway;

    private AccountRepository accountRepository;

    private DateProvider dateProvider;

    @BeforeEach
    void setUp(){
        this.authenticationGateway = new InMemoryAuthenticationGateway();
        this.accountRepository = new InMemoryAccountRepository();
        this.dateProvider = new InMemoryDateProvider(1631000000000L);
    }

    @Test
    void shouldPerformAMoneyTransferBetweenTwoAccountsInTheSameBank(){
        String customerId = "1345";
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR5030004000700000157389538";
        String senderAccountBIC = "AGRIFRPP989";
        String receiverAccountBIC = "AGRIFRPP989";
        String senderAccountId = "1";
        String receiverAccountId="2";
        String senderTransactionId = "13543A";
        String receiverTransactionId= "143E53245";
        String senderAccountFirstName = "Paul";
        String senderAccountLastName = "Duboit";
        String receiverAccountFirstName = "John";
        String receiverAccountLastName = "Smith";
        BigDecimal transactionAmount = new BigDecimal(5);

        Customer currentCustomer = new Customer(customerId,senderAccountFirstName,senderAccountLastName);
        Account existingSenderAccount = new Account.Builder()
                .withId(senderAccountId)
                .withIban(senderAccountIban)
                .withBic(senderAccountBIC)
                .withBalance(new BigDecimal(105))
                .withFirstName(senderAccountFirstName)
                .withLastName(senderAccountLastName)
                .withTransactions(new ArrayList<>(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban,receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName))))
                .withBeneficiaries(List.of(new Beneficiary("AE434", receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName)))
                .withCustomer(currentCustomer)
                .build();

        currentCustomer.addAccount(existingSenderAccount);

        Instant currentInstant = dateProvider.now();
        accountRepository.update(new Account.Builder()
                .withId(receiverAccountId)
                .withIban(receiverAccountIban)
                .withBic(receiverAccountBIC)
                .withBalance(new BigDecimal(100))
                .withFirstName(receiverAccountFirstName)
                .withLastName(receiverAccountLastName)
                .withTransactions(null)
                .withBeneficiaries(List.of())
                .build());

        authenticationGateway.authenticate(currentCustomer);


        TransferMoney transferMoney = buildTransferMoney(List.of(senderTransactionId,receiverTransactionId),List.of());

        transferMoney.handle(transactionAmount,receiverAccountIban);

        Account senderAccount = accountRepository.findByIban(senderAccountIban).orElseThrow(RuntimeException::new);
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);

      assertThat(senderAccount).usingRecursiveComparison().isEqualTo(new Account.Builder()
              .withId(senderAccountId)
              .withIban(senderAccountIban)
              .withBic(senderAccountBIC)
              .withBalance(new BigDecimal(100))
              .withFirstName(senderAccountFirstName)
              .withLastName(senderAccountLastName)
              .withTransactions(List.of(new MoneyTransferred("12345",Instant.ofEpochSecond(2534543253252L),new BigDecimal(105),receiverAccountIban,receiverAccountBIC,receiverAccountFirstName + " " + receiverAccountLastName),new MoneyTransferred(senderTransactionId,currentInstant,new BigDecimal(-5),receiverAccountIban,receiverAccountBIC,receiverAccountFirstName + " " + receiverAccountLastName)))
              .withBeneficiaries(List.of(new Beneficiary("AE434", receiverAccountIban, receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName)))
              .withCustomer(currentCustomer)
              .build());

      assertThat(receiverAccount).usingRecursiveComparison().isEqualTo(new Account.Builder()
              .withId(receiverAccountId)
              .withIban(receiverAccountIban)
              .withBic(receiverAccountBIC)
              .withBalance(new BigDecimal(105))
              .withFirstName(receiverAccountFirstName)
              .withLastName(receiverAccountLastName)
              .withTransactions(List.of(new MoneyTransferred(receiverTransactionId,currentInstant,new BigDecimal(5),senderAccountIban,senderAccountBIC,senderAccountFirstName + " " +senderAccountLastName)))
              .withBeneficiaries(List.of())
              .build());

    }

    /*
    @Test
    void shouldPerformAMoneyTransferBetweenTwoAccountsThatAreNotInTheSameBank(){
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR5030004000700000157389538";
        String senderAccountBIC = "AGRIFRPP989";
        String receiverAccountBIC = "BNPAFRPP123";
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
                .withTransactions(new ArrayList<>(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban,receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName))))
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
        assertThat(senderAccount.getTransactions()).usingRecursiveComparison().isEqualTo(List.of(new MoneyTransferred("12345",Instant.ofEpochSecond(2534543253252L),new BigDecimal(105),receiverAccountIban,receiverAccountBIC,receiverAccountFirstName + " " + receiverAccountLastName),new MoneyTransferred(senderTransactionId,currentInstant,new BigDecimal(-5),receiverAccountIban,receiverAccountBIC,receiverAccountFirstName + " " + receiverAccountLastName)));
        assertThat(extraBankTransactions).usingRecursiveComparison().isEqualTo(List.of(new MoneyTransferred(receiverTransactionId,currentInstant,new BigDecimal(5),senderAccountIban,senderAccountBIC,senderAccountFirstName + " " + senderAccountLastName)));
    }

     */
/*
    @Test
    void shouldThrowAnExceptionWhenTheReceiverAccountIsNotIsNotInTheBeneficiariesListOfTheSenderAccount(){
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR3429051014050500014M02606";
        String senderAccountBIC = "AGRIFRPP989";
        String receiverAccountBIC = "AGRIFRPP989";
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

 */
    /*
   @Test
    void shouldThrowAnExceptionWhenTheSenderAccountBalanceIsInsufficient(){
       String senderAccountIban = "FR1420041010050500013M02606";
       String receiverAccountIban = "FR5030004000700000157389538";
       String senderAccountBIC = "AGRIFRPP989";
       String receiverAccountBIC = "AGRIFRPP989";
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
               .withTransactions(new ArrayList<>(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban,receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName))))
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

     */

    /*
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

       assertThatThrownBy(()-> transferMoney.handle(senderAccountIban,transactionAmount,receiverAccountIban)).isInstanceOf(UnknownAccountWithIbanException.class)
               .hasMessage("There is no account with the iban: " + senderAccountIban);
   }

     */

    /*
    @Test
    void shouldThrowAnExceptionWhenTheReceiverAccountDoesNotExists(){
        String senderAccountIban = "FR1420041010050500013M02606";
        String receiverAccountIban = "FR5030004000700000157389538";
        String senderAccountBIC = "AGRIFRPP989";
        String receiverAccountBIC = "AGRIFRPP989";
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
                .withTransactions(new ArrayList<>(List.of(new MoneyTransferred("12345", Instant.ofEpochSecond(2534543253252L), new BigDecimal(105), receiverAccountIban,receiverAccountBIC, receiverAccountFirstName + " " + receiverAccountLastName))))
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

        assertThatThrownBy(()-> transferMoney.handle(senderAccountIban,transactionAmount,receiverAccountIban)).isInstanceOf(UnknownAccountWithIbanException.class)
                .hasMessage("There is no account with the iban: " + receiverAccountIban);
    }

     */

  private TransferMoney buildTransferMoney(List<String> ids,List<MoneyTransferred> capturedTransactions){
      IdGenerator idGenerator = new InMemoryIdGenerator(ids);
      ExtraBankTransactionsGateway extraBankTransactionsGateway = new InMemoryExtraBankTransactionsGateway(capturedTransactions);
      return  new TransferMoney(accountRepository,dateProvider,idGenerator, extraBankTransactionsGateway,authenticationGateway);
  }
}