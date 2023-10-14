package app.netlify.clementgombauld.banking.account.infra;

import app.netlify.clementgombauld.banking.account.domain.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidGenerator implements IdGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
