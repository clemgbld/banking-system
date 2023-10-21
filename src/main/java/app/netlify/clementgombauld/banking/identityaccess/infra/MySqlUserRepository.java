package app.netlify.clementgombauld.banking.identityaccess.infra;

import app.netlify.clementgombauld.banking.identityaccess.application.commands.RegisterCommand;
import app.netlify.clementgombauld.banking.identityaccess.domain.Role;
import app.netlify.clementgombauld.banking.identityaccess.domain.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MySqlUserRepository implements UserRepository {
    @Override
    public void save(RegisterCommand registerCommand, String userId, Role role) {

    }

    @Override
    public Optional<String> findEmail(String email) {
        return Optional.empty();
    }
}
