package app.netlify.clementgombauld.banking.account.unit.usecases.inMemory;

import app.netlify.clementgombauld.banking.account.domain.DateProvider;

import java.time.Instant;

public class InMemoryDateProvider implements DateProvider {
    private final long msSinceEpoch;

    public InMemoryDateProvider(long msSinceEpoch) {
        this.msSinceEpoch = msSinceEpoch;
    }

    @Override
    public Instant now() {
        return Instant.ofEpochMilli(msSinceEpoch);
    }
}
