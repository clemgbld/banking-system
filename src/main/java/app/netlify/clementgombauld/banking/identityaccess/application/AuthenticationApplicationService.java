package app.netlify.clementgombauld.banking.identityaccess.application;

import app.netlify.clementgombauld.banking.common.domain.IdGenerator;
import app.netlify.clementgombauld.banking.identityaccess.application.commands.RegisterCommand;
import app.netlify.clementgombauld.banking.identityaccess.domain.EncryptionService;
import app.netlify.clementgombauld.banking.identityaccess.domain.Role;
import app.netlify.clementgombauld.banking.identityaccess.domain.TokenGenerator;
import app.netlify.clementgombauld.banking.identityaccess.domain.UserRepository;
import app.netlify.clementgombauld.banking.identityaccess.domain.exceptions.EmailAlreadyExistsException;
import app.netlify.clementgombauld.banking.identityaccess.domain.exceptions.PasswordTooShortException;

public class AuthenticationApplicationService {

    private final TokenGenerator tokenGenerator;

    private final EncryptionService encryptionService;

    private final UserRepository userRepository;

    private final IdGenerator idGenerator;

    public AuthenticationApplicationService(TokenGenerator tokenGenerator, EncryptionService encryptionService, UserRepository userRepository, IdGenerator idGenerator) {
        this.tokenGenerator = tokenGenerator;
        this.encryptionService = encryptionService;
        this.userRepository = userRepository;
        this.idGenerator = idGenerator;
    }

    public String register(RegisterCommand registerCommand) {

        if (registerCommand.password().length() < 8) {
            throw new PasswordTooShortException();
        }

        userRepository.findEmail(registerCommand.email()).ifPresent((email) -> {
            throw new EmailAlreadyExistsException(email);
        });

        userRepository.save(
                new RegisterCommand(
                        registerCommand.firstName(),
                        registerCommand.lastName(),
                        registerCommand.email(),
                        encryptionService.encrypt(registerCommand.password())
                ),
                idGenerator.generate(),
                Role.USER
        );

        return tokenGenerator.generate(registerCommand.email());
    }
}
