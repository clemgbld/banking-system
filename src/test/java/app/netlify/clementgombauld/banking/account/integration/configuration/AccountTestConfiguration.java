package app.netlify.clementgombauld.banking.account.integration.configuration;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryAccountRepository;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryAuthenticationGateway;
import app.netlify.clementgombauld.banking.account.unit.inmemory.InMemoryBeneficiaryRepository;
import app.netlify.clementgombauld.banking.account.usecases.AddBeneficiary;
import app.netlify.clementgombauld.banking.account.usecases.DeleteBeneficiary;
import app.netlify.clementgombauld.banking.common.inmemory.DeterministicDateProvider;
import app.netlify.clementgombauld.banking.common.inmemory.InMemoryIdGenerator;
import app.netlify.clementgombauld.banking.identityaccess.infra.JwtService;
import app.netlify.clementgombauld.banking.indentityaccess.integration.inmemory.InMemoryUserDetailsService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@TestConfiguration
public class AccountTestConfiguration {

    private static final String CUSTOMER_ID = "1";
    private static final String ACCOUNT_ID = "4";
    private static final String BENEFICIARY_ID = "5";
    private static final String BENEFICIARY_IBAN = "FR1420041010050500013M02606";
    private static final String BENEFICIARY_NAME = "Arsene Lupin";
    private static final String BENEFICIARY_BIC = "BNPAFRPP123";
    private static final String ACCOUNT_IBAN = "FR5030004000700000157389538";

    public static final long CURRENT_DATE_IN_MS = 2534543253252L;

    @Bean
    AddBeneficiary addBeneficiary() {
        Iban beneficiaryIban = new Iban(BENEFICIARY_IBAN);
        BeneficiaryRepository beneficiaryRepository = new InMemoryBeneficiaryRepository();
        beneficiaryRepository.insert(ACCOUNT_ID, new Beneficiary(BENEFICIARY_ID, beneficiaryIban, new Bic(BENEFICIARY_BIC), BENEFICIARY_NAME));
        AccountRepository accountRepository = new InMemoryAccountRepository(Map.of(CUSTOMER_ID, new Account.Builder()
                .withCustomerId(CUSTOMER_ID)
                .withId(ACCOUNT_ID)
                .withIban(new Iban(ACCOUNT_IBAN))
                .build()));
        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(
                new Customer(CUSTOMER_ID, "Jean", "Charles")
        );
        return new AddBeneficiary(
                beneficiaryRepository,
                new InMemoryIdGenerator(List.of("3")),
                authenticationGateway,
                accountRepository
        );
    }

    @Bean
    DeleteBeneficiary deleteBeneficiary() {
        Iban beneficiaryIban = new Iban(BENEFICIARY_IBAN);
        BeneficiaryRepository beneficiaryRepository = new InMemoryBeneficiaryRepository();
        beneficiaryRepository.insert(ACCOUNT_ID, new Beneficiary(BENEFICIARY_ID, beneficiaryIban, new Bic(BENEFICIARY_BIC), BENEFICIARY_NAME));
        AccountRepository accountRepository = new InMemoryAccountRepository(Map.of(CUSTOMER_ID, new Account.Builder()
                .withCustomerId(CUSTOMER_ID)
                .withId(ACCOUNT_ID)
                .withIban(new Iban(ACCOUNT_IBAN))
                .build()));
        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(
                new Customer(CUSTOMER_ID, "Jean", "Charles")
        );
        return new DeleteBeneficiary(
                beneficiaryRepository,
                authenticationGateway,
                accountRepository
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
