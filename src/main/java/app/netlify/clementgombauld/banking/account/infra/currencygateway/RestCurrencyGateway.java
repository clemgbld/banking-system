package app.netlify.clementgombauld.banking.account.infra.currencygateway;

import app.netlify.clementgombauld.banking.account.domain.BankInfoType;
import app.netlify.clementgombauld.banking.account.domain.Currency;
import app.netlify.clementgombauld.banking.account.domain.CurrencyGateway;
import app.netlify.clementgombauld.banking.account.infra.exceptions.TechnicalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Component
public class RestCurrencyGateway implements CurrencyGateway {

    public static final String APP_ID = "app_id";
    public static final String BASE = "base";
    private final String currencyBaseUrl;

    private final String currencyApiKey;

    private final RestTemplate restTemplate;

    public RestCurrencyGateway(@Value("${currency_base_url}") String currencyBaseUrl, @Value("${currency_api_key}") String currencyApiKey, RestTemplate restTemplate) {
        this.currencyBaseUrl = currencyBaseUrl;
        this.currencyApiKey = currencyApiKey;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<BigDecimal> retrieveExchangeRate(Currency initialCurrency, BankInfoType targetCurrency) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(currencyBaseUrl + "/latest.json")
                .queryParam(APP_ID, currencyApiKey)
                .queryParam(BASE, initialCurrency.value());

        try {
            ResponseEntity<ExchangeRates> exchangeRates = restTemplate.getForEntity(uriBuilder.toUriString(), ExchangeRates.class);
            return translateToExchangeRate(targetCurrency, exchangeRates);

        } catch (RestClientException ex) {
            throw new TechnicalException("Failed to parse JSON response.", ex);
        }
    }

    private static Optional<BigDecimal> translateToExchangeRate(BankInfoType targetCurrency, ResponseEntity<ExchangeRates> exchangeRates) {
        return Optional.ofNullable(Objects.requireNonNull(exchangeRates.getBody()).getRates().get(targetCurrency.getValue())).map(BigDecimal::new);
    }
}
