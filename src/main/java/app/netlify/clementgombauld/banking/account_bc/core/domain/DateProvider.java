package app.netlify.clementgombauld.banking.account_bc.core.domain;

import java.time.Instant;

public interface DateProvider {
    Instant now();
}
