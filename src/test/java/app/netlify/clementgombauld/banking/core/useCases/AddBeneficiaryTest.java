package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.core.domain.exceptions.DuplicatedBeneficiaryException;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryIdGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class AddBeneficiaryTest {

    @Test
    void shouldAddBeneficiaryToTheGivenAccount(){
        String accountIban = "FR1420041010050500013M02606";
        String accountBIC = "AGRIFFRII89";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR7630004000700000157389538";
        String beneficiaryBic = "BNPAFRPP123";
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

        dataSource.put(accountId,existingSenderAccount);

        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);

        IdGenerator idGenerator = new InMemoryIdGenerator(List.of(beneficiaryId));

        AddBeneficiary addBeneficiary = new AddBeneficiary(accountRepository,idGenerator);

       String expectedId = addBeneficiary.handle(accountId,beneficiaryIban,beneficiaryBic,beneficiaryName);

        Account account = accountRepository.findById(accountId).orElseThrow(RuntimeException::new);

      assertThat(account.getBeneficiaries()).usingRecursiveComparison().isEqualTo(List.of(new Beneficiary(beneficiaryId,beneficiaryIban,beneficiaryBic,beneficiaryName)));
      assertThat(expectedId).isEqualTo(beneficiaryId);
    }

    @Test
    void shouldThrowAnExceptionWhenTheBeneficiaryHasAlreadyBeenAddedToTheAccount(){
        String accountIban = "FR1420041010050500013M02606";
        String accountBIC = "AGRIFFRII89";
        String accountId = "1";
        String accountFirstName = "Paul";
        String accountLastName = "Duboit";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR7630004000700000157389538";
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName ="Bob Dylan";

        Map<String,Account> dataSource = new HashMap<>();

        Beneficiary existingBeneficiary = new Beneficiary(beneficiaryId,beneficiaryIban,beneficiaryBic,beneficiaryName);
        List<Beneficiary> existingBeneficiaries = new ArrayList<>();
        existingBeneficiaries.add(existingBeneficiary);
        Account existingSenderAccount = new Account.Builder()
                .withId(accountId)
                .withIban(accountIban)
                .withBic(accountBIC)
                .withBalance(new BigDecimal(105))
                .withFirstName(accountFirstName)
                .withLastName(accountLastName)
                .withBeneficiaries(existingBeneficiaries)
                .build();

        dataSource.put(accountId,existingSenderAccount);

        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);

        IdGenerator idGenerator = new InMemoryIdGenerator(List.of(beneficiaryId));

        AddBeneficiary addBeneficiary = new AddBeneficiary(accountRepository,idGenerator);

      assertThatThrownBy(()-> addBeneficiary.handle(accountId,beneficiaryIban,beneficiaryBic,beneficiaryName))
              .isInstanceOf(DuplicatedBeneficiaryException.class)
              .hasMessage("The beneficiary with the iban : " + beneficiaryIban + " is already a beneficiary of the account " + accountId);
    }



}