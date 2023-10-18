package app.netlify.clementgombauld.banking.identityaccess.rest;

import app.netlify.clementgombauld.banking.identityaccess.infra.AuthenticationService;
import app.netlify.clementgombauld.banking.identityaccess.rest.in.AuthenticateRequest;
import app.netlify.clementgombauld.banking.identityaccess.rest.in.RegisterRequest;
import app.netlify.clementgombauld.banking.identityaccess.rest.out.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class IdentityAccessController {

    private final AuthenticationService service;

    @Autowired
    public IdentityAccessController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticateRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
