package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.domain.Beneficiary;
import app.netlify.clementgombauld.banking.account.domain.BeneficiaryRepository;
import app.netlify.clementgombauld.banking.account.domain.Bic;
import app.netlify.clementgombauld.banking.account.domain.Iban;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MySqlBeneficiaryRepository implements BeneficiaryRepository {

    private final JpaBeneficiaryRepository jpaBeneficiaryRepository;

    @Autowired
    public MySqlBeneficiaryRepository(JpaBeneficiaryRepository jpaBeneficiaryRepository) {
        this.jpaBeneficiaryRepository = jpaBeneficiaryRepository;
    }

    @Override
    public void insert(String accountId, Beneficiary beneficiary) {
        jpaBeneficiaryRepository.insertByAccountId(beneficiary.getId(), beneficiary.getIban(), beneficiary.getBic(), beneficiary.getName(), accountId);
    }

    @Override
    public Optional<Beneficiary> findByAccountIdAndIban(String accountId, String iban) {
        return jpaBeneficiaryRepository.findByAccountIdAndIban(accountId, iban).map(b -> new Beneficiary(b.getId(), new Iban(b.getIban()), new Bic(b.getBic()), b.getName()));
    }

    @Override
    public void delete(String accountId, String iban) {

    }
}
