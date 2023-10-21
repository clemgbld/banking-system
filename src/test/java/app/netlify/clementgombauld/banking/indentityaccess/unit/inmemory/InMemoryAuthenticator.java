package app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory;

import app.netlify.clementgombauld.banking.identityaccess.application.commands.LoginCommand;
import app.netlify.clementgombauld.banking.identityaccess.domain.Authenticator;

public class InMemoryAuthenticator implements Authenticator {
    private final boolean shouldAuthenticate;

    public InMemoryAuthenticator(boolean shouldAuthenticate) {
        this.shouldAuthenticate = shouldAuthenticate;
    }

    @Override
    public void authenticate(LoginCommand loginCommand) {
        if (shouldAuthenticate) return;
        throw new RuntimeException();
    }
}
