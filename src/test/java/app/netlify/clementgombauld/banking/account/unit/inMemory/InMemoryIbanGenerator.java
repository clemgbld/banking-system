package app.netlify.clementgombauld.banking.account.unit.inMemory;

import app.netlify.clementgombauld.banking.account.domain.Iban;
import app.netlify.clementgombauld.banking.account.domain.IbanGenerator;

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
