package app.netlify.clementgombauld.banking.account.usecases.commands;

public record CloseAccountCommand(String externalAccountIban, String externalBic, String bic, String accountName) {
}
