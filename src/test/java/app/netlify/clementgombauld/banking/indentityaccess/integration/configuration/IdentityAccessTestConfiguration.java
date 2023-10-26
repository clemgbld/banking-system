package app.netlify.clementgombauld.banking.indentityaccess.integration.configuration;

import app.netlify.clementgombauld.banking.common.inmemory.DeterministicDateProvider;
import app.netlify.clementgombauld.banking.common.inmemory.InMemoryIdGenerator;
import app.netlify.clementgombauld.banking.identityaccess.application.AuthenticationApplicationService;
import app.netlify.clementgombauld.banking.identityaccess.infra.JwtService;
import app.netlify.clementgombauld.banking.indentityaccess.integration.inmemory.InMemoryUserDetailsService;
import app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory.InMemoryAuthenticator;
import app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory.InMemoryEncryptionService;
import app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory.InMemoryTokenGenerator;
import app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory.InMemoryUserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@TestConfiguration
public class IdentityAccessTestConfiguration {

    @Bean
    public AuthenticationApplicationService authenticationApplicationService() {
        String email = "John@hotmail.fr";
        String token = "token";

        return new AuthenticationApplicationService(
                new InMemoryTokenGenerator(email, token),
                new InMemoryEncryptionService(),
                new InMemoryUserRepository(new ArrayList<>()),
                new InMemoryIdGenerator(List.of("1")),
                new InMemoryAuthenticator(true)
        );
    }

    @Bean
    public JwtService jwtService() {
        return new JwtService("secret", new DeterministicDateProvider(1243143423451224L), new InMemoryUserDetailsService(new HashMap<>()));
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(r -> r
                        .anyRequest()
                        .permitAll()

                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
