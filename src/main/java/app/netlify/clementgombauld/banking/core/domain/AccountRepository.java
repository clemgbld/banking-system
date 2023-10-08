package app.netlify.clementgombauld.banking.core.domain;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByIban(String iban);

    Optional<Account> findByCustomerId(String customerId);

    void insert(Account account);

    void update(Account... account);

    void update(Account account);

    void deleteById(String id);
}
