package app.netlify.clementgombauld.banking.identityaccess.domain;

public interface EncryptionService {
    String encrypt(Password password);
}
