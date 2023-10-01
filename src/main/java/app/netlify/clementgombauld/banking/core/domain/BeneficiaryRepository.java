package app.netlify.clementgombauld.banking.core.domain;

public interface BeneficiaryRepository {
    void insert(String accountId, Beneficiary beneficiary);
}
