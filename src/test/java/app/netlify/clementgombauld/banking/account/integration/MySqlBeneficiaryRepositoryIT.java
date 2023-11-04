package app.netlify.clementgombauld.banking.account.integration;

import app.netlify.clementgombauld.banking.account.domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
public class MySqlBeneficiaryRepositoryIT {

    private static final long CURRENT_DATE_IN_S = 95345L;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;


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


    @BeforeEach
    void setUp() {
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
    }

    @AfterEach
    void cleanUp() {
        accountRepository.deleteById("1");
    }


    @Test
    void shouldInsertABeneficiaryByItsAccountId() {
        String accountId = "1";
        String id = "235452";
        Iban iban = new Iban("FR7630066100410001057380116");
        Bic bic = new Bic("AGRIFRPP989");
        String name = "Arsene Lupin";

        beneficiaryRepository.insert(accountId, new Beneficiary(
                id,
                iban,
                bic,
                name
        ));

        Optional<Beneficiary> beneficiary = beneficiaryRepository.findByAccountIdAndIban(accountId, iban.value());

        assertThat(beneficiary.isPresent()).isTrue();
        assertThat(beneficiary.get()).isEqualTo(
                new Beneficiary(
                        id,
                        iban,
                        bic,
                        name
                )
        );
    }

    @Test
    void shouldDeleteABeneficiaryByItsAccountIdAndIban() {
        String accountId = "1";
        String id = "235452";
        Iban iban = new Iban("FR7630066100410001057380116");
        Bic bic = new Bic("AGRIFRPP989");
        String name = "Arsene Lupin";

        beneficiaryRepository.insert(accountId, new Beneficiary(
                id,
                iban,
                bic,
                name
        ));

        beneficiaryRepository.delete(accountId, iban.value());

        Optional<Beneficiary> beneficiary = beneficiaryRepository.findByAccountIdAndIban(accountId, iban.value());

        assertThat(beneficiary.isEmpty()).isTrue();
        assertThat(accountRepository.findByIban("FR1420041010050500013M02606").isPresent()).isTrue();


    }


}
