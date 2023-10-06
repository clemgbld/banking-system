package app.netlify.clementgombauld.banking.infra;

import app.netlify.clementgombauld.banking.core.domain.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidGenerator implements IdGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
