package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;

import java.util.Arrays;
import java.util.Map;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String,Account> dataSource ;

    public InMemoryAccountRepository(Map<String, Account> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Account findByIban(String iban) {
        return dataSource.get(iban);
    }

    @Override
    public void update(Account ...accounts) {
        Arrays.stream(accounts).forEach(account -> dataSource.put(account.getIban(),account));
    }
}
