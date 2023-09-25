package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryDateProvider;
import app.netlify.clementgombauld.banking.infra.inMemory.InMemoryIdGenerator;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReceiveMoneyFromExternalBankTest {

    @Test
    void shouldAddMoneyToTheExpectedBankAccountFromTheExternalBankWhenTheSenderAccountIsABeneficiary() {
        String accountId = "1";
        String receiverAccountIban = "FR1420041010050500013M02606";
        String beneficiaryId = "1234";
        String beneficiaryIban = "FR5030004000700000157389538";
        String accountName = "John Smith Junior";
        BigDecimal transactionAmount = new BigDecimal(5);
        String transactionId = "2143";
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName = "John Smith";
        long currentDateInMs = 2534543253252L;
        Instant currentDate = Instant.ofEpochMilli(currentDateInMs);

        AccountRepository accountRepository = new InMemoryAccountRepository();
        accountRepository.update(new Account.Builder()
                .withId(accountId)
                .withIban(receiverAccountIban)
                .withBeneficiaries(List.of(new Beneficiary(beneficiaryId, beneficiaryIban, beneficiaryBic, beneficiaryName)))
                .build()
        );

        IdGenerator idGenerator = new InMemoryIdGenerator(List.of(transactionId));

        DateProvider dateProvider = new InMemoryDateProvider(currentDateInMs);

        ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank = new ReceiveMoneyFromExternalBank(accountRepository, idGenerator, dateProvider);

        receiveMoneyFromExternalBank.handle(receiverAccountIban, beneficiaryIban, beneficiaryBic, accountName, transactionAmount);


        Account account = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);

        assertThat(account).usingRecursiveComparison().isEqualTo(
                new Account.Builder()
                        .withId(accountId)
                        .withBalance(transactionAmount)
                        .withIban(receiverAccountIban)
                        .withBeneficiaries(List.of(new Beneficiary(beneficiaryId, beneficiaryIban, beneficiaryBic, beneficiaryName)))
                        .withTransactions(List.of(new MoneyTransferred(transactionId, currentDate, transactionAmount, beneficiaryIban, beneficiaryBic, beneficiaryName)))
                        .build()

        );
    }
}