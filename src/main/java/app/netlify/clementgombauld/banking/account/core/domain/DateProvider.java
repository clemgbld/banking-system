package app.netlify.clementgombauld.banking.account.core.domain;

import java.time.Instant;

public interface DateProvider {
    Instant now();
}
