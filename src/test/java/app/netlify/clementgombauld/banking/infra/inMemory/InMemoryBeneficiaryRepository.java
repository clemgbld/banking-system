package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.Beneficiary;
import app.netlify.clementgombauld.banking.core.domain.BeneficiaryRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryBeneficiaryRepository implements BeneficiaryRepository {
    private final Map<String, Beneficiary> beneficiaryStore = new HashMap<>();

    public InMemoryBeneficiaryRepository() {
    }

    @Override
    public void insert(String accountId, Beneficiary beneficiary) {
        beneficiaryStore.put(accountId + beneficiary.getIban(), beneficiary);
    }

    @Override
    public Optional<Beneficiary> findByAccountIdAndIban(String accountId, String iban) {
        return Optional.ofNullable(beneficiaryStore.get(accountId + iban));
    }
}
