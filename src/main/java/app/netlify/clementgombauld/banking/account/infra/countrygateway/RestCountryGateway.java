package app.netlify.clementgombauld.banking.account.infra.countrygateway;

import app.netlify.clementgombauld.banking.account.domain.CountryGateway;
import app.netlify.clementgombauld.banking.account.domain.Currency;
import app.netlify.clementgombauld.banking.account.infra.exceptions.TechnicalException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.iban4j.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Component
public class RestCountryGateway implements CountryGateway {

    public static final String URI = "/alpha/";
    private final String countryBaseUrl;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public RestCountryGateway(@Value("${country_base_url}") String countryBaseUrl, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.countryBaseUrl = countryBaseUrl;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Currency> retrieveCurrencyByCountryCode(CountryCode countryCode) {
        String jsonString = restTemplate.getForObject(countryBaseUrl + URI + countryCode.name(), String.class);
        try {
            List<CountryInfo> countryInfos = objectMapper.readValue(jsonString, new TypeReference<>() {
            });
            return translateToCurrency(countryInfos);

        } catch (JsonProcessingException ex) {
            throw new TechnicalException("Failed to parse JSON response.", ex);
        }
    }

    private static Optional<Currency> translateToCurrency(List<CountryInfo> countryInfos) {
        return countryInfos.stream()
                .findFirst()
                .flatMap(info -> info.getCurrencies()
                        .keySet()
                        .stream()
                        .findFirst()
                        .map(Currency::new));
    }
}
