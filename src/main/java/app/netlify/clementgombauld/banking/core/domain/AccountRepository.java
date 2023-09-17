package app.netlify.clementgombauld.banking.core.domain;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByIban(String iban);
    Optional<Account> findById(String id);

    void update(Account ...account);

    void update(Account account);
}
