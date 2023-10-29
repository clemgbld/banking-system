package app.netlify.clementgombauld.banking.identityaccess.infra.db;

import app.netlify.clementgombauld.banking.identityaccess.infra.db.entity.JpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<JpaUserEntity, String> {
    Optional<JpaUserEntity> findByEmail(String email);
}
