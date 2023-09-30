package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.CountryGateway;

import java.util.Map;
import java.util.Optional;

public class InMemoryCountryGateway implements CountryGateway {
    private final Map<String, String> mapCountryCodeToCurrency;

    public InMemoryCountryGateway(Map<String, String> mapCountryCodeToCurrency) {
        this.mapCountryCodeToCurrency = mapCountryCodeToCurrency;
    }

    @Override
    public Optional<String> retrieveCurrencyByCountryCode(String countryCode) {
        return Optional.ofNullable(mapCountryCodeToCurrency.get(countryCode));
    }
}
