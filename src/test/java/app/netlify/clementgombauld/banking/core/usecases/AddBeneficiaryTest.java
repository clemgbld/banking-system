package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.DuplicatedBeneficiaryException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.InvalidBicException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.InvalidIbanException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownAccountWithIbanException;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class AddBeneficiaryTest {

    private  AuthenticationGateway authenticationGateway;
    private  AccountRepository accountRepository;

    @BeforeEach
    void setUp(){
        authenticationGateway = new InMemoryAuthenticationGateway();
        accountRepository = new InMemoryAccountRepository();
    }


    @Test
    void shouldAddBeneficiaryToTheGivenAccount(){
        String customerId = "13455";
        String accountIban = "FR1420041010050500013M02606";
        String accountBIC = "AGRIFFRII89";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR5030004000700000157389538";
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName ="Bob Dylan";

        Customer currentCustomer = new Customer(customerId,accountFirstName,accountLastName);

        Account existingSenderAccount = new Account.Builder()
                .withId(accountId)
                .withIban(accountIban)
                .withBic(accountBIC)
                .withBalance(new BigDecimal(105))
                .withFirstName(accountFirstName)
                .withLastName(accountLastName)
                .withBeneficiaries(new ArrayList<>())
                .withCustomer(currentCustomer)
                .build();

        currentCustomer.addAccount(existingSenderAccount);

        authenticationGateway.authenticate(currentCustomer);

        AddBeneficiary addBeneficiary = buildAddBeneficiary(List.of(beneficiaryId));

       String expectedId = addBeneficiary.handle(beneficiaryIban,beneficiaryBic,beneficiaryName);

        Account account = accountRepository.findByIban(accountIban).orElseThrow(RuntimeException::new);

      assertThat(account.getBeneficiaries()).usingRecursiveComparison().isEqualTo(List.of(new Beneficiary(beneficiaryId,beneficiaryIban,beneficiaryBic,beneficiaryName)));
      assertThat(expectedId).isEqualTo(beneficiaryId);
    }



    @Test
    void shouldThrowAnExceptionWhenTheBeneficiaryHasAlreadyBeenAddedToTheAccount(){
        String customerId = "13455";
        String accountIban = "FR1420041010050500013M02606";
        String accountBIC = "AGRIFFRII89";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR5030004000700000157389538";
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName ="Bob Dylan";

        Customer currentCustomer = new Customer(customerId,accountFirstName,accountLastName);


        Beneficiary existingBeneficiary = new Beneficiary(beneficiaryId,beneficiaryIban,beneficiaryBic,beneficiaryName);
        List<Beneficiary> existingBeneficiaries = new ArrayList<>();
        existingBeneficiaries.add(existingBeneficiary);
        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(accountIban)
                .withBic(accountBIC)
                .withBalance(new BigDecimal(105))
                .withFirstName(accountFirstName)
                .withLastName(accountLastName)
                .withBeneficiaries(existingBeneficiaries)
                .withCustomer(currentCustomer)
                .build();

        currentCustomer.addAccount(existingAccount);

        authenticationGateway.authenticate(currentCustomer);

        AddBeneficiary addBeneficiary = buildAddBeneficiary(List.of(beneficiaryId));

      assertThatThrownBy(()-> addBeneficiary.handle(beneficiaryIban,beneficiaryBic,beneficiaryName))
              .isInstanceOf(DuplicatedBeneficiaryException.class)
              .hasMessage("The beneficiary with the iban : " + beneficiaryIban + " is already a beneficiary of the account " + accountId);
    }




    @Test
    void shouldThrowAnInvalidIbanExceptionWhenTheBeneficiaryIbanIsNotValid(){
        String customerId = "13455";
        String accountIban = "FR1420041010050500013M02606";
        String accountBIC = "AGRIFFRII89";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR6300000070000";
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName ="Bob Dylan";

        Beneficiary existingBeneficiary = new Beneficiary(beneficiaryId,"FR5030004000700000157389538",beneficiaryBic,beneficiaryName);
        List<Beneficiary> existingBeneficiaries = new ArrayList<>();
        existingBeneficiaries.add(existingBeneficiary);
        Customer currentCustomer = new Customer(customerId,accountFirstName,accountLastName);
        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(accountIban)
                .withBic(accountBIC)
                .withBalance(new BigDecimal(105))
                .withFirstName(accountFirstName)
                .withLastName(accountLastName)
                .withBeneficiaries(existingBeneficiaries)
                .withCustomer(currentCustomer)
                .build();
        currentCustomer.addAccount(existingAccount);

        authenticationGateway.authenticate(currentCustomer);


       AddBeneficiary addBeneficiary = buildAddBeneficiary(List.of(beneficiaryId));

       assertThatThrownBy(()-> addBeneficiary.handle(beneficiaryIban,beneficiaryBic,beneficiaryName))
               .isInstanceOf(InvalidIbanException.class)
               .hasMessage("iban: " + beneficiaryIban + " is invalid.");
    }



    /*
    @Test
    void shouldThrowAnExceptionWhenTheBeneficiaryBicIsNotValid(){
        String accountIban = "FR1420041010050500013M02606";
        String accountBIC = "AGRIFFRII89";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR5030004000700000157389538";
        String beneficiaryBic = "BNPA";
        String beneficiaryName ="Bob Dylan";

        Map<String,Account> dataSource = new HashMap<>();

        Account existingSenderAccount = new Account.Builder()
                .withId(accountId)
                .withIban(accountIban)
                .withBic(accountBIC)
                .withBalance(new BigDecimal(105))
                .withFirstName(accountFirstName)
                .withLastName(accountLastName)
                .withBeneficiaries(new ArrayList<>())
                .build();

        dataSource.put(accountIban,existingSenderAccount);

        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);

        IdGenerator idGenerator = new InMemoryIdGenerator(List.of(beneficiaryId));

        AddBeneficiary addBeneficiary = new AddBeneficiary(accountRepository,idGenerator);

        assertThatThrownBy(()-> addBeneficiary.handle(accountIban,beneficiaryIban,beneficiaryBic,beneficiaryName))
                .isInstanceOf(InvalidBicException.class)
                .hasMessage("bic: " + beneficiaryBic + " is invalid.");
    }



     */

    private AddBeneficiary buildAddBeneficiary(List<String> ids){
        IdGenerator idGenerator = new InMemoryIdGenerator(ids);
        return new AddBeneficiary(accountRepository,idGenerator,authenticationGateway);
    }

}