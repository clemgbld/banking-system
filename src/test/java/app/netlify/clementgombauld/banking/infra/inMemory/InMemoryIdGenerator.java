package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.IdGenerator;

import java.util.List;

public class InMemoryIdGenerator implements IdGenerator {
    private final List<String> ids;

    public InMemoryIdGenerator(List<String> ids) {
        this.ids = ids;
    }
}
