package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.domain.Account;
import app.netlify.clementgombauld.banking.account.domain.AccountRepository;
import app.netlify.clementgombauld.banking.account.domain.Iban;
import app.netlify.clementgombauld.banking.account.infra.db.entity.JpaAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
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
        return jpaAccountRepository.findByIban(iban).map(this::toDomain);
    }

    @Override
    public Optional<Account> findByCustomerId(String customerId) {
        return jpaAccountRepository.findByCustomerId(customerId).map(this::toDomain);
    }


    @Override
    public void save(Account... accounts) {
        jpaAccountRepository.saveAll(Arrays.stream(accounts)
                .map(this::toJpa)
                .toList());
    }

    @Override
    public void save(Account account) {
        jpaAccountRepository.save(toJpa(account));
    }

    @Override
    public void deleteById(String id) {
        jpaAccountRepository.deleteById(id);
    }

    private JpaAccountEntity toJpa(Account account) {
        return new JpaAccountEntity(
                account.getId(),
                account.getCustomerId(),
                account.getIbanValue(),
                account.getBalance(),
                account.getCreationDate()
        );
    }

    private Account toDomain(JpaAccountEntity jpaAccountEntity) {
        return new Account.Builder()
                .withId(jpaAccountEntity.getId())
                .withCustomerId(jpaAccountEntity.getCustomerId())
                .withBalance(jpaAccountEntity.getBalance())
                .withCreatedOn(jpaAccountEntity.getCreatedOn())
                .withIban(new Iban(jpaAccountEntity.getIban()))
                .build();
    }
}
