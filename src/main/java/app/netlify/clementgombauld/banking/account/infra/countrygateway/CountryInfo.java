package app.netlify.clementgombauld.banking.account.infra.countrygateway;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class CountryInfo {
    @JsonProperty("currencies")
    private Map<String, Map<String, String>> currencies;

    public Map<String, Map<String, String>> getCurrencies() {
        return currencies;
    }
}
