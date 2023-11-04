package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.infra.db.entity.JpaBeneficiaryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaBeneficiaryRepository extends JpaRepository<JpaBeneficiaryEntity, String> {
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO beneficiary (id, iban, bic, name, account_id) VALUES (:id, :iban, :bic, :name, :account_id)")
    @Transactional
    void insertByAccountId(
            @Param("id") String id,
            @Param("iban") String iban,
            @Param("bic") String bic,
            @Param("name") String name,
            @Param("account_id") String accountId
    );

    @Query("SELECT b FROM JpaBeneficiaryEntity b WHERE b.account.id = :accountId AND b.iban = :iban")
    Optional<JpaBeneficiaryEntity> findByAccountIdAndIban(@Param("accountId") String accountId, @Param("iban") String iban);
}
