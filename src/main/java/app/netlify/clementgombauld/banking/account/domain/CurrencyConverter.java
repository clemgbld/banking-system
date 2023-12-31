package app.netlify.clementgombauld.banking.account.domain;

import app.netlify.clementgombauld.banking.account.domain.exceptions.CurrencyNotFoundException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.ExchangeRateNotFound;
import org.iban4j.CountryCode;

import java.math.BigDecimal;

public class CurrencyConverter {
    private final CountryGateway countryGateway;

    private final CurrencyGateway currencyGateway;

    public CurrencyConverter(CountryGateway countryGateway, CurrencyGateway currencyGateway) {
        this.countryGateway = countryGateway;
        this.currencyGateway = currencyGateway;
    }

    public BigDecimal convert(Bic bic, BigDecimal amount) {
        CountryCode countryCode = bic.getCountryCode();

        Currency currency = countryGateway.retrieveCurrencyByCountryCode(countryCode)
                .orElseThrow(() -> new CurrencyNotFoundException(countryCode.name()));

        if (currency.isBankCurrency()) return amount;

        BigDecimal exchangeRate = currencyGateway.retrieveExchangeRate(currency, BankInfoType.CURRENCY)
                .orElseThrow(() -> new ExchangeRateNotFound(currency));

        return amount.multiply(exchangeRate);
    }
}
