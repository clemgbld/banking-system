package app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory;

import app.netlify.clementgombauld.banking.identityaccess.domain.EncryptionService;
import app.netlify.clementgombauld.banking.identityaccess.domain.Password;

public class InMemoryEncryptionService implements EncryptionService {

    public static final String HASHED = "hashed";

    @Override
    public String encrypt(Password password) {
        return password.value() + HASHED;
    }
}
