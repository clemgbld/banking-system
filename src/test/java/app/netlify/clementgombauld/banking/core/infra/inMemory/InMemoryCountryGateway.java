package app.netlify.clementgombauld.banking.core.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.CountryGateway;
import app.netlify.clementgombauld.banking.core.domain.Currency;
import org.iban4j.CountryCode;

import java.util.Map;
import java.util.Optional;

public class InMemoryCountryGateway implements CountryGateway {
    private final Map<String, String> mapCountryCodeToCurrency;

    public InMemoryCountryGateway(Map<String, String> mapCountryCodeToCurrency) {
        this.mapCountryCodeToCurrency = mapCountryCodeToCurrency;
    }

    @Override
    public Optional<Currency> retrieveCurrencyByCountryCode(CountryCode countryCode) {
        Optional<String> currency = Optional.ofNullable(mapCountryCodeToCurrency.get(countryCode.name()));
        return currency.map(Currency::new);
    }
}
