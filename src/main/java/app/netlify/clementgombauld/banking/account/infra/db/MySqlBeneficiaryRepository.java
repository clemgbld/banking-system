package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.domain.Beneficiary;
import app.netlify.clementgombauld.banking.account.domain.BeneficiaryRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MySqlBeneficiaryRepository implements BeneficiaryRepository {
    @Override
    public void insert(String accountId, Beneficiary beneficiary) {

    }

    @Override
    public Optional<Beneficiary> findByAccountIdAndIban(String accountId, String iban) {
        return Optional.empty();
    }

    @Override
    public void delete(String accountId, String iban) {

    }
}
