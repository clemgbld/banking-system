package app.netlify.clementgombauld.banking.identityaccess.rest.in;

import jakarta.validation.constraints.Email;


public record AuthenticateRequest(
        @Email(message = "Email should be valid.") String email,
        String password
) {
}
