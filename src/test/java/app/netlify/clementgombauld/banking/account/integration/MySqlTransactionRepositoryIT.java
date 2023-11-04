package app.netlify.clementgombauld.banking.account.integration;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.infra.db.JpaTransactionRepository;
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
public class MySqlTransactionRepositoryIT {

    private static final long CURRENT_DATE_IN_S = 95345L;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @Autowired
    private JpaTransactionRepository jpaTransactionRepository;

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

    void shouldInsertATransaction() {
        String accountId = "1";
        String customerId = "5";
        String iban = "FR1420041010050500013M02606";
        BigDecimal balance = new BigDecimal("7.00");
        Instant createdOn = Instant.ofEpochSecond(CURRENT_DATE_IN_S);

        accountRepository.save(new Account.Builder()
                .withId(accountId)
                .withCustomerId(customerId)
                .withIban(new Iban(iban))
                .withCreatedOn(createdOn)
                .withBalance(balance)
                .build());

        String id = "12354";
        Instant creationDate = Instant.ofEpochSecond(CURRENT_DATE_IN_S);
        BigDecimal transactionAmount = new BigDecimal("5.00");
        String accountIdentifier = "FR7630066100410001057380116";
        String bic = "";
        String accountName = "Arsene Lupin";
        String reason = "shopping";

        transactionRepository.insert(accountId, new Transaction(id, creationDate, transactionAmount, accountIdentifier, bic, accountName, reason));


    }


}
