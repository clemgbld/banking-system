package app.netlify.clementgombauld.banking.core.domain.exceptions;

import app.netlify.clementgombauld.banking.core.domain.Currency;

public class ExchangeRateNotFound extends RuntimeException {
    public ExchangeRateNotFound(Currency currency) {
        super(String.format("No Exchange Rate found for this currency : %s.", currency.value()));
    }
}
