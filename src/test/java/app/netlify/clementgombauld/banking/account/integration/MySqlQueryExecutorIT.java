package app.netlify.clementgombauld.banking.account.integration;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.infra.db.JpaTransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
public class MySqlQueryExecutorIT {

    @Autowired
    private QueryExecutor queryExecutor;

    @Autowired
    private AccountRepository accountRepository;


    @Autowired
    private TransactionRepository transactionRepository;


    @Autowired
    private JpaTransactionRepository jpaTransactionRepository;

    private static final long CURRENT_DATE_IN_MS = 95345000L;

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
    void setup() {
        String accountId = "1";
        String customerId = "5";
        String iban = "FR1420041010050500013M02606";
        BigDecimal balance = new BigDecimal("7.00");
        Instant createdOn = Instant.ofEpochMilli(CURRENT_DATE_IN_MS);

        accountRepository.save(new Account.Builder()
                .withId(accountId)
                .withCustomerId(customerId)
                .withIban(new Iban(iban))
                .withCreatedOn(createdOn)
                .withBalance(balance)
                .build());

        String id = "12354";
        Instant creationDate = Instant.ofEpochSecond(95345001L);
        BigDecimal transactionAmount = new BigDecimal("5.00");
        String accountIdentifier = "FR7630066100410001057380116";
        String bic = "AGRIFRPP989";
        String accountName = "Arsene Lupin";
        String reason = "shopping";

        transactionRepository.insert(accountId, new Transaction(id, creationDate, transactionAmount, accountIdentifier, bic, accountName, reason));

        String id2 = "2345235";
        Instant creationDate2 = Instant.ofEpochSecond(95345000L);
        BigDecimal transactionAmount2 = new BigDecimal("7.00");
        String accountIdentifier2 = "FR7630066100410015043200585";
        String bic2 = "AGRIFRPP989";
        String accountName2 = "Michell Baumont";
        String reason2 = null;

        transactionRepository.insert(accountId, new Transaction(id2, creationDate2, transactionAmount2, accountIdentifier2, bic2, accountName2, reason2));

    }

    @AfterEach
    void cleanUp() {
        accountRepository.deleteById("1");
        jpaTransactionRepository.deleteById("12354");
    }
}
