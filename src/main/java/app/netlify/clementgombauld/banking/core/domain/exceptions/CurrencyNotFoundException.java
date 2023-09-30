package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(String countryCode) {
        super(String.format("No Currency found for country code : %s.", countryCode));
    }
}
