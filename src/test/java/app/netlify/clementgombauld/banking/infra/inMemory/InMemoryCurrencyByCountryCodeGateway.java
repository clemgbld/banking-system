package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.CurrencyByCountryCodeGateway;

import java.util.Map;
import java.util.Optional;

public class InMemoryCurrencyByCountryCodeGateway implements CurrencyByCountryCodeGateway {
    private final Map<String, String> mapCountryCodeToCurrency;

    public InMemoryCurrencyByCountryCodeGateway(Map<String, String> mapCountryCodeToCurrency) {
        this.mapCountryCodeToCurrency = mapCountryCodeToCurrency;
    }

    @Override
    public Optional<String> retrieve(String countryCode) {
        return Optional.ofNullable(mapCountryCodeToCurrency.get(countryCode));
    }
}
