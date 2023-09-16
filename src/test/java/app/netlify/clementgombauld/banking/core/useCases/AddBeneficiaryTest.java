package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.core.domain.Beneficiary;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAccountRepository;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class AddBeneficiaryTest {

    //@Test
    void shouldAddBeneficiaryToTheGivenAccount(){
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR7630004000700000157389538";
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName ="Bob Dylan";

        Map<String,Account> dataSource = new HashMap<>();

        AccountRepository accountRepository = new InMemoryAccountRepository(dataSource);
        String accountId = "3433";
       // dataSource.put(accountId,A)
        Account account = accountRepository.findByIban("FR7630004000700000237389538").orElseThrow(RuntimeException::new);

      assertThat(account.getBeneficiaries()).usingRecursiveComparison().isEqualTo(List.of(new Beneficiary(beneficiaryId,beneficiaryIban,beneficiaryBic,beneficiaryName)));
    }



}