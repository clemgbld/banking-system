package app.netlify.clementgombauld.banking.common.inmemory;

import app.netlify.clementgombauld.banking.common.domain.DateProvider;

import java.time.Instant;

public class DeterministicDateProvider implements DateProvider {
    private final long msSinceEpoch;

    public DeterministicDateProvider(long msSinceEpoch) {
        this.msSinceEpoch = msSinceEpoch;
    }

    @Override
    public Instant now() {
        return Instant.ofEpochMilli(msSinceEpoch);
    }
}
