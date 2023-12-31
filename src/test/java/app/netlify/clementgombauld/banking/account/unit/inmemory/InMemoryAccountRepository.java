package app.netlify.clementgombauld.banking.account.unit.inmemory;

import app.netlify.clementgombauld.banking.account.domain.Account;
import app.netlify.clementgombauld.banking.account.domain.AccountRepository;
import app.netlify.clementgombauld.banking.account.domain.Iban;

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
    public void save(Account... accounts) {
        Arrays.stream(accounts).forEach(account -> dataSource.put(account.getIbanValue(), account));
    }

    @Override
    public void save(Account account) {
        dataSource.put(account.getIbanValue(), account);
    }

    @Override
    public void deleteById(String id) {
        dataSource.remove(id);
    }


    private Optional<Account> findById(String id) {
        Account nullableAccount = dataSource.getOrDefault(id, null);
        return Objects.isNull(nullableAccount) ? Optional.empty() : Optional.of(new Account.Builder()
                .withId(nullableAccount.getId())
                .withIban(new Iban(nullableAccount.getIbanValue()))
                .withBalance(nullableAccount.getBalance())
                .withCreatedOn(nullableAccount.getCreationDate())
                .withCustomerId(nullableAccount.getCustomerId())
                .build());
    }
}
