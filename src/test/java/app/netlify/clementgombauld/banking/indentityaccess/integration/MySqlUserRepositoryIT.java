package app.netlify.clementgombauld.banking.indentityaccess.integration;

import app.netlify.clementgombauld.banking.identityaccess.application.commands.RegisterCommand;
import app.netlify.clementgombauld.banking.identityaccess.domain.Role;
import app.netlify.clementgombauld.banking.identityaccess.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
public class MySqlUserRepositoryIT {
    @Autowired
    private UserRepository userRepository;

    @Container
    private static MySQLContainer container = new MySQLContainer("mysql:8.0.26")
            .withDatabaseName("somedatabase")
            .withUsername("root")
            .withPassword("password");

    static {
        container.withCopyFileToContainer(
                MountableFile.forClasspathResource("identity-access.sql"),
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
    void shouldSaveUser() {
        userRepository.save(new RegisterCommand("Bob", "Dylan", "bobdylan@gmail.com", "password1D@"), "1", Role.USER);
        Optional<String> userEmail = userRepository.findEmail("bobdylan@gmail.com");
        assertThat(userEmail.isPresent()).isTrue();
        assertThat(userEmail.get()).isEqualTo("bobdylan@gmail.com");
    }

}
