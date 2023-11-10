package app.netlify.clementgombauld.banking.account.rest.account.out;

public record AccountDetailsDto(String firstName, String lastName, String iban, String accountNumber, String bic) {
}
