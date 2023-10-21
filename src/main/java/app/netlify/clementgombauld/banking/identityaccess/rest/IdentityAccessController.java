package app.netlify.clementgombauld.banking.identityaccess.rest;

import app.netlify.clementgombauld.banking.identityaccess.application.AuthenticationApplicationService;
import app.netlify.clementgombauld.banking.identityaccess.application.commands.LoginCommand;
import app.netlify.clementgombauld.banking.identityaccess.application.commands.RegisterCommand;
import app.netlify.clementgombauld.banking.identityaccess.rest.in.AuthenticateRequest;
import app.netlify.clementgombauld.banking.identityaccess.rest.in.RegisterRequest;
import app.netlify.clementgombauld.banking.identityaccess.rest.out.AuthenticationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class IdentityAccessController {

    private final ObjectMapper objectMapper;


    private final AuthenticationApplicationService authenticationApplicationService;

    @Autowired
    public IdentityAccessController(ObjectMapper objectMapper, AuthenticationApplicationService authenticationApplicationService) {
        this.objectMapper = objectMapper;
        this.authenticationApplicationService = authenticationApplicationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        RegisterCommand registerCommand = objectMapper.convertValue(request, RegisterCommand.class);
        return ResponseEntity.ok(new AuthenticationResponse(authenticationApplicationService.register(registerCommand)));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticateRequest request
    ) {
        LoginCommand loginCommand = objectMapper.convertValue(request, LoginCommand.class);
        return ResponseEntity.ok(new AuthenticationResponse(authenticationApplicationService.login(loginCommand)));
    }
}
