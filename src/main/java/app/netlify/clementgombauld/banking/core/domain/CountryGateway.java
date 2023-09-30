package app.netlify.clementgombauld.banking.core.domain;

import java.util.Optional;

public interface CountryGateway {
    Optional<Currency> retrieveCurrencyByCountryCode(String countryCode);
}
