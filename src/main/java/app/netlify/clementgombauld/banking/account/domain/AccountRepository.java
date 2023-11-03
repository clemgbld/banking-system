package app.netlify.clementgombauld.banking.account.domain;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByIban(String iban);

    Optional<Account> findByCustomerId(String customerId);

    void save(Account... account);

    void save(Account account);

    void deleteById(String id);
}
