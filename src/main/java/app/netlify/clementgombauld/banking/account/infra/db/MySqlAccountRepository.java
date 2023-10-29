package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.domain.Account;
import app.netlify.clementgombauld.banking.account.domain.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MySqlAccountRepository implements AccountRepository {
    @Override
    public Optional<Account> findByIban(String iban) {
        return Optional.empty();
    }

    @Override
    public Optional<Account> findByCustomerId(String customerId) {
        return Optional.empty();
    }

    @Override
    public void insert(Account account) {

    }

    @Override
    public void update(Account... account) {

    }

    @Override
    public void update(Account account) {

    }

    @Override
    public void deleteById(String id) {

    }
}
