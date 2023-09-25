package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.Account;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class ReceiveMoneyFromExternalBankTest {

    // @Test
    void shouldAddMoneyToTheExpectedBankAccountFromTheExternalBank() {
        String accountId = "1";
        String receiverAccountIban = "FR1420041010050500013M02606";


        assertThat(new Object()).usingRecursiveComparison().isEqualTo(
                new Account.Builder()
                        .withId(accountId)
                        .withIban(receiverAccountIban)
                        .withBeneficiaries(null)

        );
    }
}