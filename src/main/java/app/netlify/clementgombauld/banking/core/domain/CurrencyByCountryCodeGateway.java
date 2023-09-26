package app.netlify.clementgombauld.banking.core.domain;

import java.util.Optional;

public interface CurrencyByCountryCodeGateway {
    Optional<String> retrieve(String countryCode);
}
