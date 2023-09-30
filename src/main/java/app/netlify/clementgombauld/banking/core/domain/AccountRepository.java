package app.netlify.clementgombauld.banking.core.domain;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByIban(String iban);

    void save(Account... account);

    void save(Account account);
}
