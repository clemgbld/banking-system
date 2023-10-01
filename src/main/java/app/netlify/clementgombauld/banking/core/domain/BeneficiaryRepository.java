package app.netlify.clementgombauld.banking.core.domain;

import java.util.Optional;

public interface BeneficiaryRepository {
    void insert(String accountId, Beneficiary beneficiary);

    Optional<Beneficiary> findByAccountIdAndIban(String accountId, String iban);

    void delete(String accountId, String iban);
}
