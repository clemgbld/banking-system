package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.core.domain.Iban;

import java.util.*;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> dataSource = new HashMap<>();

    public InMemoryAccountRepository() {
    }

    @Override
    public Optional<Account> findByIban(String iban) {
        Account nullableAccount = dataSource.getOrDefault(iban, null);
        return Objects.isNull(nullableAccount) ? Optional.empty() : Optional.of(new Account.Builder()
                .withId(nullableAccount.getId())
                .withIban(new Iban(nullableAccount.getIban()))
                .withBalance(nullableAccount.getBalance())
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
