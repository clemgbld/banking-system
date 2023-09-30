package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.CurrencyNotFoundException;
import app.netlify.clementgombauld.banking.core.domain.exceptions.ExchangeRateNotFound;

import java.math.BigDecimal;

public class CurrencyConverter {
    private final CountryGateway countryGateway;

    private final CurrencyGateway currencyGateway;

    public CurrencyConverter(CountryGateway countryGateway, CurrencyGateway currencyGateway) {
        this.countryGateway = countryGateway;
        this.currencyGateway = currencyGateway;
    }

    public BigDecimal convert(Bic bic, BigDecimal amount) {
        String countryCode = bic.getCountryCode();

        String currency = countryGateway.retrieveCurrencyByCountryCode(countryCode)
                .orElseThrow(() -> new CurrencyNotFoundException(countryCode));

        if (currency.equals(BankInfoType.CURRENCY.getValue())) return amount;

        BigDecimal exchangeRate = currencyGateway.retrieveExchangeRate(currency, BankInfoType.CURRENCY.getValue())
                .orElseThrow(() -> new ExchangeRateNotFound(currency));

        return amount.multiply(exchangeRate);
    }
}
