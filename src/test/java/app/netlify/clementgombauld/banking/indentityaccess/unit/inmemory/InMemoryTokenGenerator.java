package app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory;

import app.netlify.clementgombauld.banking.identityaccess.domain.TokenGenerator;

import java.util.Map;

public class InMemoryTokenGenerator implements TokenGenerator {
    private final Map<String, String> tokenGeneratorMap;

    public InMemoryTokenGenerator(String email, String password) {
        this.tokenGeneratorMap = Map.of(email, password);
    }

    @Override
    public String generate(String email) {
        return tokenGeneratorMap.get(email);
    }
}
