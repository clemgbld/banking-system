package app.netlify.clementgombauld.banking.account.integration;

import app.netlify.clementgombauld.banking.account.domain.Account;
import app.netlify.clementgombauld.banking.account.domain.AccountRepository;
import app.netlify.clementgombauld.banking.account.domain.Iban;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
public class MySqlAccountRepositoryIT {

    private static final long CURRENT_DATE_IN_S = 95345L;

    @Autowired
    private AccountRepository accountRepository;

    @Container
    private static MySQLContainer<?> container = new MySQLContainer<>("mysql:8.0.26")
            .withDatabaseName("somedatabase")
            .withUsername("root")
            .withPassword("password");

    static {
        container.withCopyFileToContainer(
                MountableFile.forClasspathResource("account.sql"),
                "docker-entrypoint-initdb.d/"
        );
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("currency_api_key", () -> "key");
        registry.add("jwt_secret", () -> "secret");

    }

    @Test
    void shouldSaveAccount() {
        String accountId = "1";
        String customerId = "5";
        String iban = "FR1420041010050500013M02606";
        BigDecimal balance = new BigDecimal("7.00");
        Instant creationDate = Instant.ofEpochSecond(CURRENT_DATE_IN_S);

        accountRepository.save(new Account.Builder()
                .withId(accountId)
                .withCustomerId(customerId)
                .withIban(new Iban(iban))
                .withCreatedOn(creationDate)
                .withBalance(balance)
                .build());

        Optional<Account> account = accountRepository.findByIban(iban);

        assertThat(account.isPresent()).isTrue();
        assertThat(account.get()).isEqualTo(
                new Account.Builder()
                        .withId(accountId)
                        .withCustomerId(customerId)
                        .withIban(new Iban(iban))
                        .withCreatedOn(creationDate)
                        .withBalance(balance)
                        .build()
        );
    }

    @Test
    void shouldDeleteAnAccount() {
        String accountId = "5";
        String customerId = "6";
        String iban = "FR5030004000700000157389538";
        BigDecimal balance = new BigDecimal("8.00");
        Instant creationDate = Instant.ofEpochSecond(CURRENT_DATE_IN_S);

        accountRepository.save(new Account.Builder()
                .withId(accountId)
                .withCustomerId(customerId)
                .withIban(new Iban(iban))
                .withCreatedOn(creationDate)
                .withBalance(balance)
                .build());

        accountRepository.deleteById(accountId);

        Optional<Account> account = accountRepository.findByIban(iban);

        assertThat(account.isEmpty()).isTrue();


    }

    @Test
    void shouldBeAbleToSaveMultipleAccountInOneRow() {
        accountRepository.save(new Account.Builder()
                .withId("5")
                .withCustomerId("6")
                .withIban(new Iban("FR5030004000700000157389538"))
                .withCreatedOn(Instant.ofEpochSecond(CURRENT_DATE_IN_S))
                .withBalance(new BigDecimal("8.00"))
                .build(), new Account.Builder()
                .withId("23542")
                .withCustomerId("13121343")
                .withIban(new Iban("FR7630066100410001057380116"))
                .withCreatedOn(Instant.ofEpochSecond(CURRENT_DATE_IN_S))
                .withBalance(new BigDecimal("5.00"))
                .build());

        Optional<Account> account1 = accountRepository.findByIban("FR5030004000700000157389538");

        Optional<Account> account2 = accountRepository.findByIban("FR7630066100410001057380116");

        assertThat(account1.isPresent()).isTrue();
        assertThat(account1.get()).isEqualTo(new Account.Builder()
                .withId("5")
                .withCustomerId("6")
                .withIban(new Iban("FR5030004000700000157389538"))
                .withCreatedOn(Instant.ofEpochSecond(CURRENT_DATE_IN_S))
                .withBalance(new BigDecimal("8.00"))
                .build());

        assertThat(account2.isPresent()).isTrue();
        assertThat(account2.get()).isEqualTo(new Account.Builder()
                .withId("23542")
                .withCustomerId("13121343")
                .withIban(new Iban("FR7630066100410001057380116"))
                .withCreatedOn(Instant.ofEpochSecond(CURRENT_DATE_IN_S))
                .withBalance(new BigDecimal("5.00"))
                .build());

    }


}
