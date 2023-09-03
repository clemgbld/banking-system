package app.netlify.clementgombauld.banking.core.domain;

public interface AccountRepository {
    Account findByIban(String iban);

    void update(Account account);
}
