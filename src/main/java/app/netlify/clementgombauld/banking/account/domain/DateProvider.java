package app.netlify.clementgombauld.banking.account.domain;

import java.time.Instant;

public interface DateProvider {
    Instant now();
}
