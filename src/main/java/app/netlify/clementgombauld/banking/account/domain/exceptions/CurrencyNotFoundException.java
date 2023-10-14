package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(String countryCode) {
        super(String.format("No Currency found for country code : %s.", countryCode));
    }
}
