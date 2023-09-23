package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.account_bc.core.domain.*;
import app.netlify.clementgombauld.banking.account_bc.core.domain.exceptions.UnExistingAccountException;
import app.netlify.clementgombauld.banking.account_bc.core.usecases.DeleteBeneficiary;
import app.netlify.clementgombauld.banking.account_bc.core.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.account_bc.core.domain.exceptions.UnknownBeneficiaryException;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAuthenticationGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeleteBeneficiaryTest {


    private AccountRepository accountRepository;
    private AuthenticationGateway authenticationGateway;
    private DeleteBeneficiary deleteBeneficiary;

    @BeforeEach
    void setUp(){
        accountRepository = new InMemoryAccountRepository();
        authenticationGateway = new InMemoryAuthenticationGateway();
        deleteBeneficiary = new DeleteBeneficiary(accountRepository,authenticationGateway);
    }

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

        deleteBeneficiary.handle(secondBeneficiaryIban);

        Account account = accountRepository.findByIban(accountIban).orElseThrow(RuntimeException::new);

        assertThat(account.getBeneficiaries()).usingRecursiveComparison().isEqualTo(List.of(firstBeneficiary));
    }

    @Test
    void shouldThrowAnExceptionWhenThereIsNoCurrentCustomer(){
        String beneficiaryIban = "FR5030004000700000157389538";
        assertThatThrownBy(()->  deleteBeneficiary.handle(beneficiaryIban))
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheCurrentCustomerDoesNotHaveAnAccount(){
        String beneficiaryIban = "FR5030004000700000157389538";
        String customerId = "131435";
        String customerFirstName = "Paul";
        String customerLastName = "Duboit";
        Customer currentCustomer = new Customer(customerId,customerFirstName,customerLastName);
        authenticationGateway.authenticate(currentCustomer);
        assertThatThrownBy(()->  deleteBeneficiary.handle(beneficiaryIban))
                .isInstanceOf(UnExistingAccountException.class)
                .hasMessage("Customer with id: " + customerId + " does not have any account");
    }


    @Test
    void shouldThrowAnExceptionWhenTheBeneficiaryToDeleteDoesNotExist(){
        String customerId = "131435";
        String accountIban = "FR1420041010050500013M02606";
        String accountBIC = "AGRIFFRII89";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryIban = "FR5030004000700000157389538";

        Customer currentCustomer = new Customer(customerId,accountFirstName,accountLastName);

        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(accountIban)
                .withBic(accountBIC)
                .withBalance(new BigDecimal(105))
                .withFirstName(accountFirstName)
                .withLastName(accountLastName)
                .withBeneficiaries(new ArrayList<>())
                .withCustomer(currentCustomer)
                .build();

        currentCustomer.addAccount(existingAccount);

        authenticationGateway.authenticate(currentCustomer);


        assertThatThrownBy(()-> deleteBeneficiary.handle(beneficiaryIban)).isInstanceOf(UnknownBeneficiaryException.class)
                .hasMessage("Cannot find any account with the iban: " + beneficiaryIban + " in your beneficiaries list.");
    }

}