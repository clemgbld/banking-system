package app.netlify.clementgombauld.banking.core.domain;

import java.math.BigDecimal;

public class CurrencyConverter {
    private final CurrencyByCountryCodeGateway currencyByCountryCodeGateway;

    private final ExchangeRateGateway exchangeRateGateway;

    public CurrencyConverter(CurrencyByCountryCodeGateway currencyByCountryCodeGateway, ExchangeRateGateway exchangeRateGateway) {
        this.currencyByCountryCodeGateway = currencyByCountryCodeGateway;
        this.exchangeRateGateway = exchangeRateGateway;
    }

    public BigDecimal convert(Bic bic, BigDecimal amount) {
        String currency = currencyByCountryCodeGateway.retrieve(bic.getCountryCode()).orElseThrow();
        BigDecimal exchangeRate = exchangeRateGateway.retrieve(currency, BankInfoType.CURRENCY.getValue()).orElseThrow();
        return amount.multiply(exchangeRate);
    }
}
