package app.netlify.clementgombauld.banking.core.domain;

import java.math.BigDecimal;
import java.util.Optional;

public interface CurrencyGateway {
    Optional<BigDecimal> retrieveExchangeRate(String initialCurrency, String targetCurrency);
}
