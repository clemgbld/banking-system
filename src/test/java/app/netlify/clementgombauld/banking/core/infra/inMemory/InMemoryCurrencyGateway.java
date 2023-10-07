package app.netlify.clementgombauld.banking.core.infra.inMemory;

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
        Optional<Map<String, BigDecimal>> exchangeRates = Optional.ofNullable(exchangeRateStore.get(initialCurrency));
        return exchangeRates.map(stringBigDecimalMap -> stringBigDecimalMap.get(targetCurrency));
    }
}