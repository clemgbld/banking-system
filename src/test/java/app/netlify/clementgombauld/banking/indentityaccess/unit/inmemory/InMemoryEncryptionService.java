package app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory;

import app.netlify.clementgombauld.banking.identityaccess.domain.EncryptionService;

public class InMemoryEncryptionService implements EncryptionService {

    public static final String HASHED = "hashed";

    @Override
    public String encrypt(String password) {
        return password + HASHED;
    }
}
