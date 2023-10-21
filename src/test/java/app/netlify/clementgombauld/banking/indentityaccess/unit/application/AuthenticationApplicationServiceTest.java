package app.netlify.clementgombauld.banking.indentityaccess.unit.application;

import app.netlify.clementgombauld.banking.common.domain.IdGenerator;
import app.netlify.clementgombauld.banking.common.inmemory.InMemoryIdGenerator;
import app.netlify.clementgombauld.banking.identityaccess.application.AuthenticationApplicationService;
import app.netlify.clementgombauld.banking.identityaccess.application.commands.RegisterCommand;
import app.netlify.clementgombauld.banking.identityaccess.domain.EncryptionService;
import app.netlify.clementgombauld.banking.identityaccess.domain.Role;
import app.netlify.clementgombauld.banking.identityaccess.domain.TokenGenerator;
import app.netlify.clementgombauld.banking.identityaccess.domain.UserRepository;
import app.netlify.clementgombauld.banking.identityaccess.domain.exceptions.EmailAlreadyExistsException;
import app.netlify.clementgombauld.banking.identityaccess.domain.exceptions.PasswordTooShortException;
import app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory.InMemoryEncryptionService;
import app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory.InMemoryTokenGenerator;
import app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AuthenticationApplicationServiceTest {

    public static final String USER_ID = "1";

    private IdGenerator idGenerator;

    private List<Object> savedParams;

    private UserRepository userRepository;

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        idGenerator = new InMemoryIdGenerator(List.of(USER_ID));
        savedParams = new ArrayList<>();
        userRepository = new InMemoryUserRepository(savedParams);
        encryptionService = new InMemoryEncryptionService();
    }

    @Test
    void shouldRegisterUserAndGetToken() {
        String expectedToken = "token";
        String firstName = "Jean";
        String lastName = "Paul";
        String email = "jeanPaul@gmail.com";
        String password = "Dqflkjqm2433@";
        String hashedPassword = password + "hashed";

        AuthenticationApplicationService authenticationApplicationService = buildAuthenticationApplicationService(email, expectedToken);

        String actualToken = authenticationApplicationService.register(new RegisterCommand(
                firstName,
                lastName,
                email,
                password
        ));

        assertThat(savedParams).isEqualTo(List.of(new RegisterCommand(firstName,
                lastName,
                email, hashedPassword), USER_ID, Role.USER));


        assertThat(actualToken).isEqualTo(expectedToken);
    }

    @Test
    void shouldNotRegisterWhenTheEmailAlreadyExist() {
        String firstName = "Jean";
        String lastName = "Paul";
        String email = "jeanPaul@gmail.com";
        String password = "Dqflkjqm2433@";
        String hashedPassword = password + "hashed";

        userRepository.save(new RegisterCommand(
                firstName,
                lastName,
                email,
                hashedPassword
        ), USER_ID, Role.USER);

        AuthenticationApplicationService authenticationApplicationService = buildAuthenticationApplicationService(email, "token");

        assertThatThrownBy(() -> authenticationApplicationService.register(new RegisterCommand(firstName, lastName, email, password)))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Account with email " + email + " already exists.");


    }

    @Test
    void shouldNotRegisterWhenPasswordIsLessThan8Characters() {
        String firstName = "Jean";
        String lastName = "Paul";
        String email = "jeanPaul@gmail.com";
        String password = "Dqflkjq";

        AuthenticationApplicationService authenticationApplicationService = buildAuthenticationApplicationService(email, "token");

        assertThatThrownBy(() -> authenticationApplicationService.register(new RegisterCommand(firstName, lastName, email, password)))
                .isInstanceOf(PasswordTooShortException.class)
                .hasMessage("Password must be at least 8 characters long");

    }

    private AuthenticationApplicationService buildAuthenticationApplicationService(String email, String token) {
        TokenGenerator tokenGenerator = new InMemoryTokenGenerator(email, token);
        return new AuthenticationApplicationService(tokenGenerator, encryptionService, userRepository, idGenerator);
    }

}
