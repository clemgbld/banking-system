package app.netlify.clementgombauld.banking.account.infra.countrygateway;

import app.netlify.clementgombauld.banking.account.domain.CountryGateway;
import app.netlify.clementgombauld.banking.account.domain.Currency;
import org.iban4j.CountryCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class RestCountriesGateway implements CountryGateway {


    private final String countryBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public RestCountriesGateway(@Value("${country_base_url}") String countryBaseUrl) {
        this.countryBaseUrl = countryBaseUrl;
    }

    @Override
    public Optional<Currency> retrieveCurrencyByCountryCode(CountryCode countryCode) {
        System.out.println(countryBaseUrl);
        var x = restTemplate.getForObject(countryBaseUrl + "/alpha/" + countryCode.name(), Object.class);
        return Optional.empty();
    }
}
