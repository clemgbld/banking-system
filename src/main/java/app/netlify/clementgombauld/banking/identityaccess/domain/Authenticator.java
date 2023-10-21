package app.netlify.clementgombauld.banking.identityaccess.domain;

import app.netlify.clementgombauld.banking.identityaccess.application.commands.LoginCommand;

public interface Authenticator {
    void authenticate(LoginCommand loginCommand);
}
