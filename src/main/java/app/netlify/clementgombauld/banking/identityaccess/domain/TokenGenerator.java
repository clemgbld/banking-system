package app.netlify.clementgombauld.banking.identityaccess.domain;

public interface TokenGenerator {
    String generate(String email);
}
