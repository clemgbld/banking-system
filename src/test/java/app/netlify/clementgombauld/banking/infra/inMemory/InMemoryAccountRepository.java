package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> dataSource = new HashMap<>();

    public InMemoryAccountRepository() {
    }

    @Override
    public Optional<Account> findByIban(String iban) {
        Account nullableAccount = dataSource.getOrDefault(iban, null);
        if (nullableAccount == null) return Optional.empty();
        return Optional.of(new Account.Builder()
                .withId(nullableAccount.getId())
                .withIban(nullableAccount.getIban())
                .withBic(nullableAccount.getBic())
                .withBalance(nullableAccount.getBalance())
                .withTransactions(nullableAccount.getTransactions())
                .withBeneficiaries(nullableAccount.getBeneficiaries())
                .withCustomer(nullableAccount.getCustomer())
                .build());
    }


    @Override
    public void update(Account... accounts) {
        Arrays.stream(accounts).forEach(account -> dataSource.put(account.getIban(), account));
    }

    @Override
    public void update(Account account) {
        dataSource.put(account.getIban(), account);
    }
}
