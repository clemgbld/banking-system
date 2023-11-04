package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.infra.db.entity.JpaAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaAccountRepository extends JpaRepository<JpaAccountEntity, String> {
    Optional<JpaAccountEntity> findByIban(String iban);

    Optional<JpaAccountEntity> findByCustomerId(String customerId);
}
