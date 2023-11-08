package app.netlify.clementgombauld.banking.account.integration.configuration;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.TransactionDto;
import app.netlify.clementgombauld.banking.account.unit.inmemory.*;
import app.netlify.clementgombauld.banking.account.usecases.commands.*;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverview;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverviewQuery;
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

import java.math.BigDecimal;
import java.util.ArrayList;
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
    CloseAccount closeAccount() {
        Iban beneficiaryIban = new Iban(BENEFICIARY_IBAN);
        BeneficiaryRepository beneficiaryRepository = new InMemoryBeneficiaryRepository();
        beneficiaryRepository.insert(ACCOUNT_ID, new Beneficiary(BENEFICIARY_ID, beneficiaryIban, new Bic(BENEFICIARY_BIC), BENEFICIARY_NAME));
        Map<String, Account> accountStore = new HashMap<>();
        accountStore.put(CUSTOMER_ID, new Account.Builder()
                .withCustomerId(CUSTOMER_ID)
                .withId(ACCOUNT_ID)
                .withIban(new Iban(ACCOUNT_IBAN))
                .withBalance(new BigDecimal(6))
                .build());
        AccountRepository accountRepository = new InMemoryAccountRepository(accountStore);
        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(
                new Customer(CUSTOMER_ID, "Jean", "Charles")
        );
        return new CloseAccount(
                accountRepository,
                authenticationGateway,
                new InMemoryExternalBankTransactionsGateway(new ArrayList<>(), new ArrayList<>()),
                new DeterministicDateProvider(CURRENT_DATE_IN_MS),
                new InMemoryIdGenerator(List.of("13544", "253425")),
                new InMemoryTransactionRepository(new HashMap<>())
        );
    }

    @Bean
    TransferMoney transferMoney() {
        Iban beneficiaryIban = new Iban(BENEFICIARY_IBAN);
        BeneficiaryRepository beneficiaryRepository = new InMemoryBeneficiaryRepository();
        beneficiaryRepository.insert(ACCOUNT_ID, new Beneficiary(BENEFICIARY_ID, beneficiaryIban, new Bic("AGRIFRPP989"), BENEFICIARY_NAME));
        Map<String, Account> accountStore = new HashMap<>();
        accountStore.put(CUSTOMER_ID, new Account.Builder()
                .withCustomerId(CUSTOMER_ID)
                .withId(ACCOUNT_ID)
                .withIban(new Iban(ACCOUNT_IBAN))
                .withBalance(new BigDecimal(6))
                .build());
        AccountRepository accountRepository = new InMemoryAccountRepository(accountStore);
        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(
                new Customer(CUSTOMER_ID, "Jean", "Charles")
        );
        return new TransferMoney(
                accountRepository,
                beneficiaryRepository,
                new InMemoryTransactionRepository(new HashMap<>()),
                new DeterministicDateProvider(CURRENT_DATE_IN_MS),
                new InMemoryIdGenerator(List.of("12434", "25342", "2453245", "253425")),
                new InMemoryExternalBankTransactionsGateway(new ArrayList<>(), new ArrayList<>()),
                authenticationGateway
        );
    }

    @Bean
    ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank() {
        Iban beneficiaryIban = new Iban(BENEFICIARY_IBAN);
        BeneficiaryRepository beneficiaryRepository = new InMemoryBeneficiaryRepository();
        beneficiaryRepository.insert(ACCOUNT_ID, new Beneficiary(BENEFICIARY_ID, beneficiaryIban, new Bic("AGRIFRPP989"), BENEFICIARY_NAME));
        Map<String, Account> accountStore = new HashMap<>();
        accountStore.put(ACCOUNT_IBAN, new Account.Builder()
                .withCustomerId(CUSTOMER_ID)
                .withId(ACCOUNT_ID)
                .withIban(new Iban(ACCOUNT_IBAN))
                .withBalance(new BigDecimal(6))
                .build());
        AccountRepository accountRepository = new InMemoryAccountRepository(accountStore);

        return new ReceiveMoneyFromExternalBank(
                accountRepository,
                new InMemoryTransactionRepository(new HashMap<>()),
                beneficiaryRepository,
                new InMemoryIdGenerator(List.of("5435")),
                new DeterministicDateProvider(CURRENT_DATE_IN_MS),
                new InMemoryCountryGateway(Map.of("FR", "EUR")),
                new InMemoryCurrencyGateway(Map.of("USD", Map.of("EUR", new BigDecimal(1))))
        );
    }

    @Bean
    GetAccountOverview accountOverview() {
        String customerId = "235432";
        AuthenticationGateway authenticationGateway = new InMemoryAuthenticationGateway(new Customer(customerId, "John", "Smith"));
        QueryExecutor queryExecutor = new InMemoryQueryExecutor(Map.of(
                new GetAccountOverviewQuery(customerId, 3),
                new AccountWithTransactionsDto(
                        "FR1420041010050500013M02606",
                        new BigDecimal("5.00"),
                        List.of(new TransactionDto(
                                "234535",
                                "Michel Baumont",
                                95345000L,
                                new BigDecimal("6.00"),
                                "shopping"
                        ))
                )
        ));
        return new GetAccountOverview(authenticationGateway, queryExecutor);
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
