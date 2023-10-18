package app.netlify.clementgombauld.banking.identityaccess.rest.in;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
