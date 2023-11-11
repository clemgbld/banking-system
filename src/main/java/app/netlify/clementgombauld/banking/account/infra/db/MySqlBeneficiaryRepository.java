package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.domain.Beneficiary;
import app.netlify.clementgombauld.banking.account.domain.BeneficiaryRepository;
import app.netlify.clementgombauld.banking.account.domain.Bic;
import app.netlify.clementgombauld.banking.account.domain.Iban;
import app.netlify.clementgombauld.banking.account.infra.db.entity.JpaBeneficiaryEntity;
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
        return jpaBeneficiaryRepository.findByAccountIdAndIban(accountId, iban).map(this::toDomain);
    }

    @Override
    public void delete(String accountId, String iban) {
        jpaBeneficiaryRepository.deleteByAccountIdAndIban(accountId, iban);
    }

    private Beneficiary toDomain(JpaBeneficiaryEntity jpaBeneficiaryEntity) {
        return new Beneficiary(jpaBeneficiaryEntity.getId(), new Iban(jpaBeneficiaryEntity.getIban()), new Bic(jpaBeneficiaryEntity.getBic()), jpaBeneficiaryEntity.getName(), new Iban(jpaBeneficiaryEntity.getAccountIban()));
    }
}
