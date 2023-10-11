package app.netlify.clementgombauld.banking.core.domain;

import org.iban4j.CountryCode;

import java.util.Optional;

public interface CountryGateway {
    Optional<Currency> retrieveCurrencyByCountryCode(CountryCode countryCode);
}
