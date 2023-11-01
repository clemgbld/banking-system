package app.netlify.clementgombauld.banking.account.rest.account.in;


public record CloseAccountRequest(String externalAccountIban,
                                  String externalBic,
                                  String accountName) {
}
