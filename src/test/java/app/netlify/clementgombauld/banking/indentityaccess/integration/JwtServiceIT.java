package app.netlify.clementgombauld.banking.indentityaccess.integration;

import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import app.netlify.clementgombauld.banking.common.inmemory.DeterministicDateProvider;
import app.netlify.clementgombauld.banking.identityaccess.infra.JwtService;
import app.netlify.clementgombauld.banking.indentityaccess.integration.inmemory.InMemoryUserDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.Instant;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


public class JwtServiceIT {

    private final String jwtSecret = "secret";

    private final long currentDateInMs = 1631000000000L;

    private final Instant currentDate = Instant.ofEpochMilli(1631000000000L);

    private DateProvider dateProvider;

    private HashMap<String, UserDetails> userDetailsStore;

    private UserDetailsService userDetailsService;

    @BeforeEach
    void setup() {
        dateProvider = new DeterministicDateProvider(currentDateInMs);
        userDetailsStore = new HashMap<>();
        userDetailsService = new InMemoryUserDetailsService(userDetailsStore);
    }


    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldHaveAToken() {
        JwtService jwtService = buildJwtService();
        assertThat(jwtService.hasNoToken("Bearer some-token")).isFalse();
    }

    @Test
    void shouldNotHaveAToken() {
        JwtService jwtService = buildJwtService();
        assertThat(jwtService.hasNoToken(null)).isTrue();
    }

    @Test
    void shouldNotHaveATokenWhenTheAuthorizationHeaderDoesNotStartWithBearer() {
        JwtService jwtService = buildJwtService();
        assertThat(jwtService.hasNoToken("Something ")).isTrue();
    }

    private JwtService buildJwtService() {
        return new JwtService(jwtSecret, dateProvider, userDetailsService);
    }


}
