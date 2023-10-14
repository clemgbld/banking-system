package app.netlify.clementgombauld.banking.account.unit.usecases;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.domain.exceptions.*;
import app.netlify.clementgombauld.banking.account.unit.usecases.inMemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.account.unit.usecases.inMemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.account.unit.usecases.inMemory.InMemoryBeneficiaryRepository;
import app.netlify.clementgombauld.banking.account.usecases.DeleteBeneficiary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeleteBeneficiaryTest {
    private BeneficiaryRepository beneficiaryRepository;
    private AuthenticationGateway authenticationGateway;

    @BeforeEach
    void setUp() {
        beneficiaryRepository = new InMemoryBeneficiaryRepository();
        authenticationGateway = new InMemoryAuthenticationGateway();
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

        Beneficiary firstBeneficiary = new Beneficiary(firstBeneficiaryId, new Iban(firstBeneficiaryIban), new Bic(firstBeneficiaryBic), firstBeneficiaryName);
        Beneficiary secondBeneficiary = new Beneficiary(secondBeneficiaryId, new Iban(secondBeneficiaryIban), new Bic(secondBeneficiaryBic), secondBeneficiaryName);
        beneficiaryRepository.insert(accountId, firstBeneficiary);
        beneficiaryRepository.insert(accountId, secondBeneficiary);

        Map<String, Account> accountStore = new HashMap<>();

        Customer currentCustomer = new Customer(customerId, accountFirstName, accountLastName);

        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(105))
                .build();

        accountStore.put(customerId, existingAccount);


        authenticationGateway.authenticate(currentCustomer);

        DeleteBeneficiary deleteBeneficiary = buildDeleteBeneficiary(accountStore);

        deleteBeneficiary.handle(secondBeneficiaryIban);


        assertThat(beneficiaryRepository.findByAccountIdAndIban(accountId, secondBeneficiaryIban))
                .isEmpty();
    }

    @Test
    void shouldThrowAnExceptionWhenThereIsNoCurrentCustomer() {
        String beneficiaryIban = "FR5030004000700000157389538";
        DeleteBeneficiary deleteBeneficiary = buildDeleteBeneficiary(new HashMap<>());
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
        DeleteBeneficiary deleteBeneficiary = buildDeleteBeneficiary(new HashMap<>());
        assertThatThrownBy(() -> deleteBeneficiary.handle(beneficiaryIban))
                .isInstanceOf(UnknownAccountWithCustomerId.class)
                .hasMessage("There is no account with the customerId: " + customerId);
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
        Map<String, Account> accountStore = new HashMap<>();
        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(105))
                .build();
        accountStore.put(customerId, existingAccount);

        authenticationGateway.authenticate(currentCustomer);

        DeleteBeneficiary deleteBeneficiary = buildDeleteBeneficiary(accountStore);

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
        Map<String, Account> accountStore = new HashMap<>();
        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(105))
                .build();

        accountStore.put(customerId, existingAccount);

        authenticationGateway.authenticate(currentCustomer);

        DeleteBeneficiary deleteBeneficiary = buildDeleteBeneficiary(accountStore);

        assertThatThrownBy(() -> deleteBeneficiary.handle(beneficiaryIban))
                .isInstanceOf(InvalidIbanException.class)
                .hasMessage("accountIdentifier: " + beneficiaryIban + " is invalid.");
    }

    private DeleteBeneficiary buildDeleteBeneficiary(Map<String, Account> accountStore) {
        AccountRepository accountRepository = new InMemoryAccountRepository(accountStore);
        return new DeleteBeneficiary(beneficiaryRepository, authenticationGateway, accountRepository);
    }

}