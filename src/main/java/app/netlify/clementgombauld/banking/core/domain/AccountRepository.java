package app.netlify.clementgombauld.banking.core.domain;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByIban(String iban);

    void update(Account ...account);
}
