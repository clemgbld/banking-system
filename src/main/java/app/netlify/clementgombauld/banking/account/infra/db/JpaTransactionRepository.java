package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.infra.db.entity.JpaTransactionEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;

@Repository
public interface JpaTransactionRepository extends JpaRepository<JpaTransactionEntity, String> {

    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO transaction (id,creation_date,transaction_amount,account_identifier,bic,account_name,reason,account_id) VALUES (:id,:creation_date,:transaction_amount,:account_identifier,:bic,:account_name,:reason,:account_id)")
    @Transactional
    void insertByAccountId(
            @Param("id") String id,
            @Param("creation_date") Instant creationDate,
            @Param("transaction_amount") BigDecimal transactionAmount,
            @Param("account_identifier") String accountIdentifier,
            @Param("bic") String bic,
            @Param("account_name") String accountName,
            @Param("reason") String reason,
            @Param("account_id") String accountId
    );
}
