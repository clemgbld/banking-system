package app.netlify.clementgombauld.banking.account.integration.configuration;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryBeneficiaryRepository;
import app.netlify.clementgombauld.banking.account.usecases.AddBeneficiary;
import app.netlify.clementgombauld.banking.common.inmemory.InMemoryIdGenerator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;


@TestConfiguration
public class AccountTestConfiguration {

    @Bean
    AddBeneficiary addBeneficiary() {
        String customerId = "1";
        String accountId = "4";
        String beneficiaryId = "5";
        Iban beneficiaryIban = new Iban("FR1420041010050500013M02606");
        BeneficiaryRepository beneficiaryRepository = new InMemoryBeneficiaryRepository();
        beneficiaryRepository.insert(accountId, new Beneficiary(beneficiaryId, beneficiaryIban, new Bic("BNPAFRPP123"), "Arsene Lupin"));
        AccountRepository accountRepository = new InMemoryAccountRepository();
        accountRepository.insert(new Account.Builder()
                .withCustomerId(customerId)
                .withId(accountId)
                .withIban(new Iban("FR5030004000700000157389538"))
                .build()
        );
        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(
                new Customer(customerId, "Jean", "Charles")
        );
        return new AddBeneficiary(
                beneficiaryRepository,
                new InMemoryIdGenerator(List.of("3")),
                authenticationGateway,
                accountRepository
        );
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
