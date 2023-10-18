package app.netlify.clementgombauld.banking.identityaccess.rest.in;

public record AuthenticateRequest(
        String email,
        String password
) {
}
