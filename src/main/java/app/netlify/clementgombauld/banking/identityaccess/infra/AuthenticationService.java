package app.netlify.clementgombauld.banking.identityaccess.infra;

import app.netlify.clementgombauld.banking.common.domain.IdGenerator;
import app.netlify.clementgombauld.banking.identityaccess.infra.entity.JpaUserEntity;
import app.netlify.clementgombauld.banking.identityaccess.domain.Role;
import app.netlify.clementgombauld.banking.identityaccess.rest.in.AuthenticateRequest;
import app.netlify.clementgombauld.banking.identityaccess.rest.in.RegisterRequest;
import app.netlify.clementgombauld.banking.identityaccess.rest.out.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JpaUserRepository jpaUserRepository;

    private final IdGenerator idGenerator;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(JpaUserRepository jpaUserRepository, IdGenerator idGenerator, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jpaUserRepository = jpaUserRepository;
        this.idGenerator = idGenerator;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        JpaUserEntity jpaUserEntity = new JpaUserEntity(
                idGenerator.generate(),
                request.firstName(),
                request.lastName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.USER
        );

        jpaUserRepository.save(jpaUserEntity);

        return new AuthenticationResponse(jwtService.generateToken(jpaUserEntity));
    }

    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        JpaUserEntity jpaUserEntity = jpaUserRepository.findByEmail(request.email()).orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return new AuthenticationResponse(jwtService.generateToken(jpaUserEntity));
    }
}

