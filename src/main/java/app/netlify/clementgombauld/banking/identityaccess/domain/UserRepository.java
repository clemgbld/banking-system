package app.netlify.clementgombauld.banking.identityaccess.domain;

import app.netlify.clementgombauld.banking.identityaccess.application.commands.RegisterCommand;

public interface UserRepository {
    void save(RegisterCommand registerCommand, String userId, Role role);
}
