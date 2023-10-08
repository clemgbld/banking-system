package app.netlify.clementgombauld.banking.core.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.core.domain.Iban;

import java.util.*;

public class InMemoryAccountRepository implements AccountRepository {
    private Map<String, Account> dataSource = new HashMap<>();

    public InMemoryAccountRepository(Map<String, Account> dataSource) {
        this.dataSource = dataSource;
    }

    public InMemoryAccountRepository() {
    }

    @Override
    public Optional<Account> findByIban(String iban) {
        return findById(iban);
    }

    @Override
    public Optional<Account> findByCustomerId(String customerId) {
        return findById(customerId);
    }

    @Override
    public void insert(Account account) {
        dataSource.put(account.getIban(), account);
    }


    @Override
    public void update(Account... accounts) {
        Arrays.stream(accounts).forEach(account -> dataSource.put(account.getIban(), account));
    }

    @Override
    public void update(Account account) {
        dataSource.put(account.getIban(), account);
    }

    @Override
    public void deleteByCustomerId(String customerId) {
        dataSource.remove(customerId);
    }


    private Optional<Account> findById(String id) {
        Account nullableAccount = dataSource.getOrDefault(id, null);
        return Objects.isNull(nullableAccount) ? Optional.empty() : Optional.of(new Account.Builder()
                .withId(nullableAccount.getId())
                .withIban(new Iban(nullableAccount.getIban()))
                .withBalance(nullableAccount.getBalance())
                .withCreatedOn(nullableAccount.getCreationDate())
                .build());
    }
}
