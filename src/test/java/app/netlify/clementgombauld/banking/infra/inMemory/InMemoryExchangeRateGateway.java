package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.ExchangeRateGateway;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class InMemoryExchangeRateGateway implements ExchangeRateGateway {

    private final Map<String, Map<String, BigDecimal>> exchangeRateStore;

    public InMemoryExchangeRateGateway(Map<String, Map<String, BigDecimal>> exchangeRateStore) {
        this.exchangeRateStore = exchangeRateStore;
    }

    @Override
    public Optional<BigDecimal> retrieve(String initialCurrency, String targetCurrency) {
        return Optional.ofNullable(exchangeRateStore.get(initialCurrency).get(targetCurrency));
    }
}
