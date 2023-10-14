package app.netlify.clementgombauld.banking.account.domain.exceptions;

import app.netlify.clementgombauld.banking.account.domain.Currency;

public class ExchangeRateNotFound extends RuntimeException {
    public ExchangeRateNotFound(Currency currency) {
        super(String.format("No Exchange Rate found for this currency : %s.", currency.value()));
    }
}
