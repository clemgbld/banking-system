package app.netlify.clementgombauld.banking.account.infra.countrygateway;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class CountryInfo {
    @JsonProperty("name")
    private Map<String, String> name;

    @JsonProperty("tld")
    private String[] tld;

    @JsonProperty("cca2")
    private String cca2;

    @JsonProperty("ccn3")
    private String ccn3;

    @JsonProperty("cca3")
    private String cca3;

    @JsonProperty("cioc")
    private String cioc;

    @JsonProperty("independent")
    private boolean independent;

    @JsonProperty("status")
    private String status;

    @JsonProperty("unMember")
    private boolean unMember;

    @JsonProperty("currencies")
    private Map<String, Map<String, String>> currencies;

    @JsonProperty("idd")
    private Map<String, Object> idd;

    @JsonProperty("capital")
    private String[] capital;

    @JsonProperty("altSpellings")
    private String[] altSpellings;

    @JsonProperty("region")
    private String region;

    @JsonProperty("subregion")
    private String subregion;

    @JsonProperty("languages")
    private Map<String, String> languages;

    @JsonProperty("translations")
    private Map<String, Map<String, String>> translations;

    @JsonProperty("latlng")
    private double[] latlng;

    @JsonProperty("landlocked")
    private boolean landlocked;

    @JsonProperty("borders")
    private String[] borders;

    @JsonProperty("area")
    private double area;

    @JsonProperty("demonyms")
    private Map<String, Map<String, String>> demonyms;

    @JsonProperty("flag")
    private String flag;

    @JsonProperty("maps")
    private Map<String, String> maps;

    @JsonProperty("population")
    private long population;

    @JsonProperty("gini")
    private Map<String, Double> gini;

    @JsonProperty("fifa")
    private String fifa;

    @JsonProperty("car")
    private Map<String, Object> car;

    @JsonProperty("timezones")
    private String[] timezones;

    @JsonProperty("continents")
    private String[] continents;

    @JsonProperty("flags")
    private Map<String, String> flags;

    @JsonProperty("coatOfArms")
    private Map<String, String> coatOfArms;

    @JsonProperty("startOfWeek")
    private String startOfWeek;

    @JsonProperty("capitalInfo")
    private Map<String, double[]> capitalInfo;

    @JsonProperty("postalCode")
    private Map<String, String> postalCode;

    public Map<String, Map<String, String>> getCurrencies() {
        return currencies;
    }
}
