package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.CurrencyGateway;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class InMemoryCurrencyGateway implements CurrencyGateway {

    private final Map<String, Map<String, BigDecimal>> exchangeRateStore;

    public InMemoryCurrencyGateway(Map<String, Map<String, BigDecimal>> exchangeRateStore) {
        this.exchangeRateStore = exchangeRateStore;
    }

    @Override
    public Optional<BigDecimal> retrieveExchangeRate(String initialCurrency, String targetCurrency) {
        return Optional.ofNullable(exchangeRateStore.get(initialCurrency).get(targetCurrency));
    }
}
