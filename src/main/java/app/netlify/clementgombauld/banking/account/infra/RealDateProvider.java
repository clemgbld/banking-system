package app.netlify.clementgombauld.banking.account.infra;

import app.netlify.clementgombauld.banking.account.domain.DateProvider;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RealDateProvider implements DateProvider {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
