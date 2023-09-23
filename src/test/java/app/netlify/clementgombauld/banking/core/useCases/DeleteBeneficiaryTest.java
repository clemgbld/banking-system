package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownAccountWithIbanException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownBeneficiaryException;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAuthenticationGateway;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class DeleteBeneficiaryTest {

    @Test
    void shouldDeleteTheExpectedBeneficiary(){
        String customerId = "131435";
        String accountIban = "FR1420041010050500013M02606";
        String accountBIC = "AGRIFFRII89";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String firstBeneficiaryId = "1234";
        String firstBeneficiaryIban = "FR5030004000700000157389538";
        String firstBeneficiaryBic = "BNPAFRPP123";
        String firstBeneficiaryName ="Bob Dylan";
        String secondBeneficiaryId = "3455";
        String secondBeneficiaryIban = "FR2330004000700000157389539";
        String secondBeneficiaryBic = "BNPAFRPP123";
        String secondBeneficiaryName ="Luc Smith";

        Beneficiary firstBeneficiary = new Beneficiary(firstBeneficiaryId,firstBeneficiaryIban,firstBeneficiaryBic,firstBeneficiaryName);
        Beneficiary secondBeneficiary = new Beneficiary(secondBeneficiaryId,secondBeneficiaryIban,secondBeneficiaryBic,secondBeneficiaryName);

        List<Beneficiary> existingBeneficiaries = new ArrayList<>(List.of(firstBeneficiary, secondBeneficiary));

        Map<String,Account> dataSource = new HashMap<>();

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

        AuthenticationGateway authenticationGateway =  new InMemoryAuthenticationGateway();

        authenticationGateway.authenticate(currentCustomer);

        dataSource.put(accountIban,existingAccount);

        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);

        DeleteBeneficiary deleteBeneficiary = new DeleteBeneficiary(accountRepository,authenticationGateway);

        deleteBeneficiary.handle(secondBeneficiaryIban);

        Account account = accountRepository.findByIban(accountIban).orElseThrow(RuntimeException::new);

        assertThat(account.getBeneficiaries()).usingRecursiveComparison().isEqualTo(List.of(firstBeneficiary));
    }
/*
    @Test
    void shouldThrowAnExceptionWhenTheAccountIsUnknown(){
        String accountIban = "FR1420041010050500013M02606";
        String beneficiaryIban = "FR5030004000700000157389538";


        Map<String,Account> dataSource = new HashMap<>();


        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);

        DeleteBeneficiary deleteBeneficiary = new DeleteBeneficiary(accountRepository);

        assertThatThrownBy(()-> deleteBeneficiary.handle(accountIban,beneficiaryIban))
                .isInstanceOf(UnknownAccountWithIbanException.class)
                .hasMessage("There is no account with the iban: " + accountIban);
    }

 */

    /*
    @Test
    void shouldThrowAnExceptionWhenTheBeneficiaryToDeleteDoesNotExist(){
        String accountIban = "FR1420041010050500013M02606";
        String accountBIC = "AGRIFFRII89";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryIban = "FR5030004000700000157389538";

        Map<String,Account> dataSource = new HashMap<>();

        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(accountIban)
                .withBic(accountBIC)
                .withBalance(new BigDecimal(105))
                .withFirstName(accountFirstName)
                .withLastName(accountLastName)
                .withBeneficiaries(new ArrayList<>())
                .build();

        dataSource.put(accountIban,existingAccount);

        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);

        DeleteBeneficiary deleteBeneficiary = new DeleteBeneficiary(accountRepository);

        assertThatThrownBy(()-> deleteBeneficiary.handle(accountIban,beneficiaryIban)).isInstanceOf(UnknownBeneficiaryException.class)
                .hasMessage("Cannot find any account with the iban: " + beneficiaryIban + " in your beneficiaries list.");
    }
*/
}