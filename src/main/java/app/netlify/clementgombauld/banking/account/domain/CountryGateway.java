package app.netlify.clementgombauld.banking.account.domain;

import org.iban4j.CountryCode;

import java.util.Optional;

public interface CountryGateway {
    Optional<Currency> retrieveCurrencyByCountryCode(CountryCode countryCode);
}
