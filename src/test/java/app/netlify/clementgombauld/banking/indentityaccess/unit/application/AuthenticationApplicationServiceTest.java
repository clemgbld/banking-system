package app.netlify.clementgombauld.banking.indentityaccess.unit.application;

import app.netlify.clementgombauld.banking.common.domain.IdGenerator;
import app.netlify.clementgombauld.banking.common.inmemory.InMemoryIdGenerator;
import app.netlify.clementgombauld.banking.identityaccess.application.AuthenticationApplicationService;
import app.netlify.clementgombauld.banking.identityaccess.application.commands.RegisterCommand;
import app.netlify.clementgombauld.banking.identityaccess.domain.EncryptionService;
import app.netlify.clementgombauld.banking.identityaccess.domain.Role;
import app.netlify.clementgombauld.banking.identityaccess.domain.TokenGenerator;
import app.netlify.clementgombauld.banking.identityaccess.domain.UserRepository;
import app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory.InMemoryEncryptionService;
import app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory.InMemoryTokenGenerator;
import app.netlify.clementgombauld.banking.indentityaccess.unit.inmemory.InMemoryUserRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationApplicationServiceTest {

    @Test
    void shouldRegisterUserAndGetToken() {
        String userId = "1";
        String expectedToken = "token";
        String firstName = "Jean";
        String lastName = "Paul";
        String email = "jeanPaul@gmail.com";
        String password = "Dqflkjqm2433@";
        String hashedPassword = password + "hashed";


        TokenGenerator tokenGenerator = new InMemoryTokenGenerator(email, expectedToken);

        EncryptionService encryptionService = new InMemoryEncryptionService();

        List<Object> savedParams = new ArrayList<>();

        UserRepository userRepository = new InMemoryUserRepository(savedParams);

        IdGenerator idGenerator = new InMemoryIdGenerator(List.of(userId));

        AuthenticationApplicationService authenticationApplicationService = new AuthenticationApplicationService(tokenGenerator, encryptionService, userRepository, idGenerator);


        String actualToken = authenticationApplicationService.register(new RegisterCommand(
                firstName,
                lastName,
                email,
                password
        ));

        assertThat(savedParams).isEqualTo(List.of(new RegisterCommand(firstName,
                lastName,
                email, hashedPassword), userId, Role.USER));


        assertThat(actualToken).isEqualTo(expectedToken);
    }

}
