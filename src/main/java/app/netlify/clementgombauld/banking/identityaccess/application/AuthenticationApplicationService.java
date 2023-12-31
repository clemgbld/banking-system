package app.netlify.clementgombauld.banking.identityaccess.application;

import app.netlify.clementgombauld.banking.common.domain.IdGenerator;
import app.netlify.clementgombauld.banking.identityaccess.application.commands.LoginCommand;
import app.netlify.clementgombauld.banking.identityaccess.application.commands.RegisterCommand;
import app.netlify.clementgombauld.banking.identityaccess.domain.*;
import app.netlify.clementgombauld.banking.identityaccess.domain.exceptions.EmailAlreadyExistsException;


public class AuthenticationApplicationService {

    private final TokenGenerator tokenGenerator;

    private final EncryptionService encryptionService;

    private final UserRepository userRepository;

    private final IdGenerator idGenerator;

    private final Authenticator authenticator;

    public AuthenticationApplicationService(TokenGenerator tokenGenerator, EncryptionService encryptionService, UserRepository userRepository, IdGenerator idGenerator, Authenticator authenticator) {
        this.tokenGenerator = tokenGenerator;
        this.encryptionService = encryptionService;
        this.userRepository = userRepository;
        this.idGenerator = idGenerator;
        this.authenticator = authenticator;
    }

    public String register(RegisterCommand registerCommand) {

        Password validPassword = new Password(registerCommand.password());

        userRepository.findEmail(registerCommand.email()).ifPresent((email) -> {
            throw new EmailAlreadyExistsException(email);
        });

        userRepository.save(
                new RegisterCommand(
                        registerCommand.firstName(),
                        registerCommand.lastName(),
                        registerCommand.email(),
                        encryptionService.encrypt(validPassword)
                ),
                idGenerator.generate(),
                Role.USER
        );

        return tokenGenerator.generate(registerCommand.email());
    }

    public String login(LoginCommand loginCommand) {
        authenticator.authenticate(loginCommand);
        return tokenGenerator.generate(loginCommand.email());
    }
}
