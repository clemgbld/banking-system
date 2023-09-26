package app.netlify.clementgombauld.banking.core.domain;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangeRateGateway {
    Optional<BigDecimal> retrieve(String initialCurrency, String targetCurrency);
}
