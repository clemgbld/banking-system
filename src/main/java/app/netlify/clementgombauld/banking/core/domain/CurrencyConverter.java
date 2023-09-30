package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.NoCurrencyFoundException;

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
                .orElseThrow(() -> new NoCurrencyFoundException(countryCode));

        BigDecimal exchangeRate = currencyGateway.retrieveExchangeRate(currency, BankInfoType.CURRENCY.getValue()).orElseThrow();

        return amount.multiply(exchangeRate);
    }
}
