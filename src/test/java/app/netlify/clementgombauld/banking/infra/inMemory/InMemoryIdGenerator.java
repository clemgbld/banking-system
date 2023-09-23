package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.account.core.domain.IdGenerator;

import java.util.List;

public class InMemoryIdGenerator implements IdGenerator {
    private final List<String> ids;


    private int count;

    public InMemoryIdGenerator(List<String> ids) {
        this.ids = ids;
    }


    @Override
    public String generate() {
        String id = ids.get(count);
        count++;
        return id;
    }
}
