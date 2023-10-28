package app.netlify.clementgombauld.banking.identityaccess.infra;

import app.netlify.clementgombauld.banking.identityaccess.application.commands.RegisterCommand;
import app.netlify.clementgombauld.banking.identityaccess.domain.Role;
import app.netlify.clementgombauld.banking.identityaccess.domain.UserRepository;
import app.netlify.clementgombauld.banking.identityaccess.infra.entity.JpaUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MySqlUserRepository implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Autowired
    public MySqlUserRepository(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }


    @Override
    public void save(RegisterCommand registerCommand, String userId, Role role) {
        JpaUserEntity jpaUserEntity = new JpaUserEntity(userId,
                registerCommand.firstName(),
                registerCommand.lastName(),
                registerCommand.email(),
                registerCommand.password()
                , role);

        jpaUserRepository.save(jpaUserEntity);
    }

    @Override
    public Optional<String> findEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(JpaUserEntity::getEmail);
    }
}
