package app.netlify.clementgombauld.banking.identityaccess.infra;

import app.netlify.clementgombauld.banking.identityaccess.infra.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
