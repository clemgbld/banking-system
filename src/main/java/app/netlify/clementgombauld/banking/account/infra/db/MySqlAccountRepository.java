package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.domain.Account;
import app.netlify.clementgombauld.banking.account.domain.AccountRepository;
import app.netlify.clementgombauld.banking.account.domain.Iban;
import app.netlify.clementgombauld.banking.account.infra.db.entity.JpaAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MySqlAccountRepository implements AccountRepository {

    private final JpaAccountRepository jpaAccountRepository;

    @Autowired
    public MySqlAccountRepository(JpaAccountRepository jpaAccountRepository) {
        this.jpaAccountRepository = jpaAccountRepository;
    }

    @Override
    public Optional<Account> findByIban(String iban) {
        return jpaAccountRepository.findByIban(iban).map(a -> new Account.Builder()
                .withId(a.getId())
                .withCustomerId(a.getCustomerId())
                .withBalance(a.getBalance())
                .withCreatedOn(a.getCreatedOn())
                .withIban(new Iban(a.getIban()))
                .build());
    }

    @Override
    public Optional<Account> findByCustomerId(String customerId) {
        return Optional.empty();
    }

    @Override
    public void insert(Account account) {
        jpaAccountRepository.save(
                new JpaAccountEntity(
                        account.getId(),
                        account.getCustomerId(),
                        account.getIban(),
                        account.getBalance(),
                        account.getCreationDate()
                )
        );
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
