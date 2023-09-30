package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class NoCurrencyFoundException extends RuntimeException {
    public NoCurrencyFoundException(String countryCode) {
        super(String.format("No Currency found for country code : %s.", countryCode));
    }
}
