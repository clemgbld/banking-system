package app.netlify.clementgombauld.banking.common.infra;

import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RealDateProvider implements DateProvider {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
