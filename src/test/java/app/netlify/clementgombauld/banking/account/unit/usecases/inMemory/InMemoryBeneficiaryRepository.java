package app.netlify.clementgombauld.banking.account.unit.usecases.inMemory;

import app.netlify.clementgombauld.banking.account.domain.Beneficiary;
import app.netlify.clementgombauld.banking.account.domain.BeneficiaryRepository;

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

    @Override
    public void delete(String accountId, String iban) {
        beneficiaryStore.remove(accountId + iban);
    }
}
