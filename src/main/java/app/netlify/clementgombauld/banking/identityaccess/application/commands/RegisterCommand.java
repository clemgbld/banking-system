package app.netlify.clementgombauld.banking.identityaccess.application.commands;

public record RegisterCommand(String firstName, String lastName, String email, String password) {
}
