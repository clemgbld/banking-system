package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class ExchangeRateNotFound extends RuntimeException {
    public ExchangeRateNotFound(String currency) {
        super(String.format("No Exchange Rate found for this currency : %s.", currency));
    }
}
