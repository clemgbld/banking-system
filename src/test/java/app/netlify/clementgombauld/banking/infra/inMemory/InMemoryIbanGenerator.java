package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.Iban;
import app.netlify.clementgombauld.banking.core.domain.IbanGenerator;

public class InMemoryIbanGenerator implements IbanGenerator {
    private final String generatedIban;

    public InMemoryIbanGenerator(String generatedIban) {
        this.generatedIban = generatedIban;
    }

    @Override
    public Iban generate() {
        return new Iban(generatedIban);
    }
}
