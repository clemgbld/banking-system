package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.Beneficiary;
import app.netlify.clementgombauld.banking.core.domain.BeneficiaryRepository;

import java.util.Map;

public class InMemoryBeneficiaryRepository implements BeneficiaryRepository {
    private final Map<String, Beneficiary> beneficiaryStore;

    public InMemoryBeneficiaryRepository(Map<String, Beneficiary> beneficiaryStore) {
        this.beneficiaryStore = beneficiaryStore;
    }

    @Override
    public void insert(String accountId, Beneficiary beneficiary) {
        beneficiaryStore.put(accountId, beneficiary);
    }
}
