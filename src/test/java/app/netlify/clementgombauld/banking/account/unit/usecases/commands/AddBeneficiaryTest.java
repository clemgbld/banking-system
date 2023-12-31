package app.netlify.clementgombauld.banking.account.unit.usecases.commands;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.domain.exceptions.*;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryBeneficiaryRepository;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.account.usecases.commands.AddBeneficiaryCommand;
import app.netlify.clementgombauld.banking.common.inmemory.InMemoryIdGenerator;
import app.netlify.clementgombauld.banking.account.usecases.commands.AddBeneficiary;
import app.netlify.clementgombauld.banking.common.domain.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class AddBeneficiaryTest {


    private BeneficiaryRepository beneficiaryRepository;

    @BeforeEach
    void setUp() {
        beneficiaryRepository = new InMemoryBeneficiaryRepository();
    }


    @Test
    void shouldAddBeneficiaryToTheGivenAccount() {
        String customerId = "13455";
        String accountIban = "FR1420041010050500013M02606";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR5030004000700000157389538";
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName = "Bob Dylan";

        Map<String, Account> accountStore = new HashMap<>();

        Customer currentCustomer = new Customer(customerId, accountFirstName, accountLastName);

        Account existingSenderAccount = new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(105))
                .build();

        accountStore.put(customerId, existingSenderAccount);


        AddBeneficiary addBeneficiary = buildAddBeneficiary(accountStore, List.of(beneficiaryId), currentCustomer);

        String expectedId = addBeneficiary.handle(new AddBeneficiaryCommand(beneficiaryIban, beneficiaryBic, beneficiaryName));

        assertThat(beneficiaryRepository.findByAccountIdAndIban(accountId, beneficiaryIban).orElseThrow())
                .isEqualTo(new Beneficiary(beneficiaryId, new Iban(beneficiaryIban), new Bic(beneficiaryBic), beneficiaryName, new Iban(accountIban)));

        assertThat(expectedId).isEqualTo(beneficiaryId);
    }


    @Test
    void shouldThrowAnExceptionWhenTheBeneficiaryHasAlreadyBeenAddedToTheAccount() {
        String customerId = "13455";
        String accountIban = "FR1420041010050500013M02606";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR5030004000700000157389538";
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName = "Bob Dylan";

        Customer currentCustomer = new Customer(customerId, accountFirstName, accountLastName);

        Map<String, Account> accountStore = new HashMap<>();

        Beneficiary existingBeneficiary = new Beneficiary(beneficiaryId, new Iban(beneficiaryIban), new Bic(beneficiaryBic), beneficiaryName, new Iban(accountIban));

        beneficiaryRepository.insert(accountId, existingBeneficiary);

        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(105))
                .build();

        accountStore.put(customerId, existingAccount);


        AddBeneficiary addBeneficiary = buildAddBeneficiary(accountStore, List.of(beneficiaryId), currentCustomer);

        assertThatThrownBy(() -> addBeneficiary.handle(new AddBeneficiaryCommand(beneficiaryIban, beneficiaryBic, beneficiaryName)))
                .isInstanceOf(DuplicatedBeneficiaryException.class)
                .hasMessage("The beneficiary with the accountIdentifier : " + beneficiaryIban + " is already a beneficiary of the account " + accountId);
    }


    @Test
    void shouldThrowAnInvalidIbanExceptionWhenTheBeneficiaryIbanIsNotValid() {
        String customerId = "13455";
        String accountIban = "FR1420041010050500013M02606";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR6300000070000";
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName = "Bob Dylan";

        Map<String, Account> accountStore = new HashMap<>();

        Customer currentCustomer = new Customer(customerId, accountFirstName, accountLastName);
        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(105))
                .build();
        accountStore.put(customerId, existingAccount);


        AddBeneficiary addBeneficiary = buildAddBeneficiary(accountStore, List.of(beneficiaryId), currentCustomer);

        assertThatThrownBy(() -> addBeneficiary.handle(new AddBeneficiaryCommand(beneficiaryIban, beneficiaryBic, beneficiaryName)))
                .isInstanceOf(InvalidIbanException.class)
                .hasMessage("accountIdentifier: " + beneficiaryIban + " is invalid.");
    }

    @Test
    void shouldNotAddBeneficiaryWhenBeneficiaryIbanIsTheSameThanAccountIban() {
        String customerId = "13455";
        String accountIban = "FR1420041010050500013M02606";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR1420041010050500013M02606";
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName = "Bob Dylan";

        Map<String, Account> accountStore = new HashMap<>();

        Customer currentCustomer = new Customer(customerId, accountFirstName, accountLastName);
        Account existingAccount = new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(105))
                .build();
        accountStore.put(customerId, existingAccount);

        AddBeneficiary addBeneficiary = buildAddBeneficiary(accountStore, List.of(beneficiaryId), currentCustomer);

        assertThatThrownBy(() -> addBeneficiary.handle(new AddBeneficiaryCommand(beneficiaryIban, beneficiaryBic, beneficiaryName)))
                .isInstanceOf(SameIbanThanAccountException.class)
                .hasMessage("You can't add yourself as a beneficiary.");


    }

    @Test
    void shouldThrowAnExceptionWhenThereIsNoCurrentCustomer() {
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR6300000070000";
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName = "Bob Dylan";
        AddBeneficiary addBeneficiary = buildAddBeneficiary(new HashMap<>(), List.of(beneficiaryId), null);
        assertThatThrownBy(() -> addBeneficiary.handle(new AddBeneficiaryCommand(beneficiaryIban, beneficiaryBic, beneficiaryName)))
                .isInstanceOf(NoCurrentCustomerException.class)
                .hasMessage("No current customer authenticated.");
    }


    @Test
    void shouldThrowAnExceptionWhenTheBeneficiaryBicIsNotValid() {
        String customerId = "13455";
        String accountIban = "FR1420041010050500013M02606";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR5030004000700000157389538";
        String beneficiaryBic = "BNPA";
        String beneficiaryName = "Bob Dylan";

        Map<String, Account> accountStore = new HashMap<>();

        Customer currentCustomer = new Customer(customerId, accountFirstName, accountLastName);

        Account existingSenderAccount = new Account.Builder()
                .withId(accountId)
                .withIban(new Iban(accountIban))
                .withBalance(new BigDecimal(105))
                .build();

        accountStore.put(customerId, existingSenderAccount);


        AddBeneficiary addBeneficiary = buildAddBeneficiary(accountStore, List.of(beneficiaryId), currentCustomer);

        assertThatThrownBy(() -> addBeneficiary.handle(new AddBeneficiaryCommand(beneficiaryIban, beneficiaryBic, beneficiaryName)))
                .isInstanceOf(InvalidBicException.class)
                .hasMessage("bic: " + beneficiaryBic + " is invalid.");
    }

    @Test
    void shouldThrowAnExceptionWhenTheAccountDoesNotExists() {
        String customerId = "13455";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR5030004000700000157389538";
        String beneficiaryBic = "BNPA";
        String beneficiaryName = "Bob Dylan";

        Customer currentCustomer = new Customer(customerId, accountFirstName, accountLastName);

        AddBeneficiary addBeneficiary = buildAddBeneficiary(new HashMap<>(), List.of(beneficiaryId), currentCustomer);

        assertThatThrownBy(() -> addBeneficiary.handle(new AddBeneficiaryCommand(beneficiaryIban, beneficiaryBic, beneficiaryName)))
                .isInstanceOf(UnknownAccountWithCustomerId.class)
                .hasMessage("There is no account with the customerId: " + customerId);
    }


    private AddBeneficiary buildAddBeneficiary(Map<String, Account> accountStore, List<String> ids, Customer customer) {
        IdGenerator idGenerator = new InMemoryIdGenerator(ids);
        AccountRepository accountRepository = new InMemoryAccountRepository(accountStore);
        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(customer);
        return new AddBeneficiary(beneficiaryRepository, idGenerator, authenticationGateway, accountRepository);
    }

}