package app.netlify.clementgombauld.banking.account_bc.core.domain;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByIban(String iban);

    void update(Account ...account);

    void update(Account account);
}
