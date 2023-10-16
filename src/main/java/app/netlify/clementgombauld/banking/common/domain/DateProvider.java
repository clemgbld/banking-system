package app.netlify.clementgombauld.banking.common.domain;

import java.time.Instant;

public interface DateProvider {
    Instant now();
}
