package app.netlify.clementgombauld.banking.core.domain;

import java.math.BigDecimal;

public class CurrencyConverter {
    private final CountryGateway countryGateway;

    private final CurrencyGateway currencyGateway;

    public CurrencyConverter(CountryGateway countryGateway, CurrencyGateway currencyGateway) {
        this.countryGateway = countryGateway;
        this.currencyGateway = currencyGateway;
    }

    public BigDecimal convert(Bic bic, BigDecimal amount) {
        String currency = countryGateway.retrieveCurrencyByCountryCode(bic.getCountryCode()).orElseThrow();
        BigDecimal exchangeRate = currencyGateway.retrieveExchangeRate(currency, BankInfoType.CURRENCY.getValue()).orElseThrow();
        return amount.multiply(exchangeRate);
    }
}
