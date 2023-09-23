package app.netlify.clementgombauld.banking.core.domain;

import java.time.Instant;

public interface DateProvider {
    Instant now();
}
