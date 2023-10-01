package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.InvalidIbanException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnExistingAccountException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.NoCurrentCustomerException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownBeneficiaryException;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryBeneficiaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeleteBeneficiaryTest {
    private BeneficiaryRepository beneficiaryRepository;
    private AuthenticationGateway authenticationGateway;
    private DeleteBeneficiary deleteBeneficiary;

    @BeforeEach
    void setUp() {
        beneficiaryRepository = new InMemoryBeneficiaryRepository();
        authenticationGateway = new InMemoryAuthenticationGateway();
        deleteBeneficiary = new DeleteBeneficiary(beneficiaryRepository, authenticationGateway);
    }

    @Test
    void shouldDeleteTheExpectedBeneficiary() {
        String customerId = "131435";
        String accountIban = "FR1420041010050500013M02606";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String firstBeneficiaryId = "1234";
        String firstBeneficiaryIban = "FR5030004000700000157389538";
        String firstBeneficiaryBic = "BNPAFRPP123";
        String firstBeneficiaryName = "Bob Dylan";
        String secondBeneficiaryId = "3455";
        String secondBeneficiaryIban = "FR2330004000700000157389539";
        String secondBeneficiaryBic = "BNPAFRPP123";
        String secondBeneficiaryName = "Luc Smith";

        Beneficiary firstBeneficiary = new Beneficiary(firstBeneficiaryId, firstBeneficiaryIban, firstBeneficiaryBic, firstBeneficiaryName);
        Beneficiary secondBeneficiary = new Beneficiary(secondBeneficiaryId, secondBeneficiaryIban, secondBeneficiaryBic, secondBeneficiaryName);
        beneficiaryRepository.insert(accountId, firstBeneficiary);
        beneficiaryRepository.insert(accountId, secondBeneficiary);


        Customer currentCustomer = new Customer(customerId, accountFirstName, accountLastName);

        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(accountIban)
                .withBalance(new BigDecimal(105))
                .build();

        currentCustomer.addAccount(existingAccount);

        authenticationGateway.authenticate(currentCustomer);

        deleteBeneficiary.handle(secondBeneficiaryIban);


        assertThat(beneficiaryRepository.findByAccountIdAndIban(accountId, secondBeneficiaryIban))
                .isEmpty();
    }

    @Test
    void shouldThrowAnExceptionWhenThereIsNoCurrentCustomer() {
        String beneficiaryIban = "FR5030004000700000157389538";
        assertThatThrownBy(() -> deleteBeneficiary.handle(beneficiaryIban))
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheCurrentCustomerDoesNotHaveAnAccount() {
        String beneficiaryIban = "FR5030004000700000157389538";
        String customerId = "131435";
        String customerFirstName = "Paul";
        String customerLastName = "Duboit";
        Customer currentCustomer = new Customer(customerId, customerFirstName, customerLastName);
        authenticationGateway.authenticate(currentCustomer);
        assertThatThrownBy(() -> deleteBeneficiary.handle(beneficiaryIban))
                .isInstanceOf(UnExistingAccountException.class)
                .hasMessage("Customer with id: " + customerId + " does not have any account");
    }


    @Test
    void shouldThrowAnExceptionWhenTheBeneficiaryToDeleteDoesNotExist() {
        String customerId = "131435";
        String accountIban = "FR1420041010050500013M02606";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryIban = "FR5030004000700000157389538";

        Customer currentCustomer = new Customer(customerId, accountFirstName, accountLastName);

        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(accountIban)
                .withBalance(new BigDecimal(105))
                .build();

        currentCustomer.addAccount(existingAccount);

        authenticationGateway.authenticate(currentCustomer);


        assertThatThrownBy(() -> deleteBeneficiary.handle(beneficiaryIban)).isInstanceOf(UnknownBeneficiaryException.class)
                .hasMessage("Cannot find any account with the accountIdentifier: " + beneficiaryIban + " in your beneficiaries list.");
    }


    @Test
    void shouldThrowAnExceptionWhenTheIbanIsNotValid() {
        String customerId = "131435";
        String accountIban = "FR1420041010050500013M02606";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryIban = "FR50300040007000001573895";

        Customer currentCustomer = new Customer(customerId, accountFirstName, accountLastName);

        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(accountIban)
                .withBalance(new BigDecimal(105))
                .build();

        currentCustomer.addAccount(existingAccount);

        authenticationGateway.authenticate(currentCustomer);


        assertThatThrownBy(() -> deleteBeneficiary.handle(beneficiaryIban))
                .isInstanceOf(InvalidIbanException.class)
                .hasMessage("accountIdentifier: " + beneficiaryIban + " is invalid.");
    }

}