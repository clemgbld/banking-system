package app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory;

import app.netlify.clementgombauld.banking.identityaccess.application.commands.RegisterCommand;
import app.netlify.clementgombauld.banking.identityaccess.domain.Role;
import app.netlify.clementgombauld.banking.identityaccess.domain.UserRepository;

import java.util.List;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {
    private final List<Object> savedParams;

    public InMemoryUserRepository(List<Object> savedParams) {
        this.savedParams = savedParams;
    }

    @Override
    public void save(RegisterCommand registerCommand, String userId, Role role) {
        savedParams.add(registerCommand);
        savedParams.add(userId);
        savedParams.add(role);
    }

    @Override
    public Optional<String> findEmail(String email) {
        if (savedParams.size() == 0) return Optional.empty();
        RegisterCommand registerCommand = (RegisterCommand) savedParams.get(0);
        return Optional.of(registerCommand.email());
    }
}
