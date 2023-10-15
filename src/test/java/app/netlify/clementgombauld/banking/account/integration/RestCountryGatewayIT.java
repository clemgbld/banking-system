package app.netlify.clementgombauld.banking.account.integration;

import app.netlify.clementgombauld.banking.account.domain.CountryGateway;
import app.netlify.clementgombauld.banking.account.domain.Currency;
import app.netlify.clementgombauld.banking.account.infra.countrygateway.RestCountryGateway;
import app.netlify.clementgombauld.banking.account.infra.exceptions.TechnicalException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.iban4j.CountryCode;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class RestCountryGatewayIT extends WireMockTestClass {
    private CountryGateway countryGateway;

    @BeforeEach
    void setUp() {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        countryGateway = new RestCountryGateway(wireMockServer.baseUrl(), new RestTemplate(), objectMapper);
    }


    @Test
    void shouldRetrieveTheExpectedCurrency() {
        wireMockServer.stubFor(
                WireMock.get("/alpha/FR")
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("[{\"name\":{\"common\":\"France\",\"official\":\"French Republic\",\"nativeName\":{\"fra\":{\"official\":\"République française\",\"common\":\"France\"}}},\"tld\":[\".fr\"],\"cca2\":\"FR\",\"ccn3\":\"250\",\"cca3\":\"FRA\",\"cioc\":\"FRA\",\"independent\":true,\"status\":\"officially-assigned\",\"unMember\":true,\"currencies\":{\"EUR\":{\"name\":\"Euro\",\"symbol\":\"€\"}},\"idd\":{\"root\":\"+3\",\"suffixes\":[\"3\"]},\"capital\":[\"Paris\"],\"altSpellings\":[\"FR\",\"French Republic\",\"République française\"],\"region\":\"Europe\",\"subregion\":\"Western Europe\",\"languages\":{\"fra\":\"French\"},\"translations\":{\"ara\":{\"official\":\"الجمهورية الفرنسية\",\"common\":\"فرنسا\"},\"bre\":{\"official\":\"Republik Frañs\",\"common\":\"Frañs\"},\"ces\":{\"official\":\"Francouzská republika\",\"common\":\"Francie\"},\"cym\":{\"official\":\"French Republic\",\"common\":\"France\"},\"deu\":{\"official\":\"Französische Republik\",\"common\":\"Frankreich\"},\"est\":{\"official\":\"Prantsuse Vabariik\",\"common\":\"Prantsusmaa\"},\"fin\":{\"official\":\"Ranskan tasavalta\",\"common\":\"Ranska\"},\"fra\":{\"official\":\"République française\",\"common\":\"France\"},\"hrv\":{\"official\":\"Francuska Republika\",\"common\":\"Francuska\"},\"hun\":{\"official\":\"Francia Köztársaság\",\"common\":\"Franciaország\"},\"ita\":{\"official\":\"Repubblica francese\",\"common\":\"Francia\"},\"jpn\":{\"official\":\"フランス共和国\",\"common\":\"フランス\"},\"kor\":{\"official\":\"프랑스 공화국\",\"common\":\"프랑스\"},\"nld\":{\"official\":\"Franse Republiek\",\"common\":\"Frankrijk\"},\"per\":{\"official\":\"جمهوری فرانسه\",\"common\":\"فرانسه\"},\"pol\":{\"official\":\"Republika Francuska\",\"common\":\"Francja\"},\"por\":{\"official\":\"República Francesa\",\"common\":\"França\"},\"rus\":{\"official\":\"Французская Республика\",\"common\":\"Франция\"},\"slk\":{\"official\":\"Francúzska republika\",\"common\":\"Francúzsko\"},\"spa\":{\"official\":\"República francés\",\"common\":\"Francia\"},\"srp\":{\"official\":\"Француска Република\",\"common\":\"Француска\"},\"swe\":{\"official\":\"Republiken Frankrike\",\"common\":\"Frankrike\"},\"tur\":{\"official\":\"Fransa Cumhuriyeti\",\"common\":\"Fransa\"},\"urd\":{\"official\":\"جمہوریہ فرانس\",\"common\":\"فرانس\"},\"zho\":{\"official\":\"法兰西共和国\",\"common\":\"法国\"}},\"latlng\":[46.0,2.0],\"landlocked\":false,\"borders\":[\"AND\",\"BEL\",\"DEU\",\"ITA\",\"LUX\",\"MCO\",\"ESP\",\"CHE\"],\"area\":551695.0,\"demonyms\":{\"eng\":{\"f\":\"French\",\"m\":\"French\"},\"fra\":{\"f\":\"Française\",\"m\":\"Français\"}},\"flag\":\"\\uD83C\\uDDEB\\uD83C\\uDDF7\",\"maps\":{\"googleMaps\":\"https://goo.gl/maps/g7QxxSFsWyTPKuzd7\",\"openStreetMaps\":\"https://www.openstreetmap.org/relation/1403916\"},\"population\":67391582,\"gini\":{\"2018\":32.4},\"fifa\":\"FRA\",\"car\":{\"signs\":[\"F\"],\"side\":\"right\"},\"timezones\":[\"UTC-10:00\",\"UTC-09:30\",\"UTC-09:00\",\"UTC-08:00\",\"UTC-04:00\",\"UTC-03:00\",\"UTC+01:00\",\"UTC+02:00\",\"UTC+03:00\",\"UTC+04:00\",\"UTC+05:00\",\"UTC+10:00\",\"UTC+11:00\",\"UTC+12:00\"],\"continents\":[\"Europe\"],\"flags\":{\"png\":\"https://flagcdn.com/w320/fr.png\",\"svg\":\"https://flagcdn.com/fr.svg\",\"alt\":\"The flag of France is composed of three equal vertical bands of blue, white and red.\"},\"coatOfArms\":{\"png\":\"https://mainfacts.com/media/images/coats_of_arms/fr.png\",\"svg\":\"https://mainfacts.com/media/images/coats_of_arms/fr.svg\"},\"startOfWeek\":\"monday\",\"capitalInfo\":{\"latlng\":[48.87,2.33]},\"postalCode\":{\"format\":\"#####\",\"regex\":\"^(\\\\d{5})$\"}}]"))

        );

        var actualCurrency = countryGateway.retrieveCurrencyByCountryCode(CountryCode.FR);

        assertThat(actualCurrency.isPresent()).isTrue();
        assertThat(actualCurrency.get()).isEqualTo(new Currency("EUR"));
    }

    @Test
    void shouldThrowATechnicalExceptionWhenTheJsonParsingFail() {
        wireMockServer.stubFor(
                WireMock.get("/alpha/FR")
                        .willReturn(WireMock.aResponse()
                                .withStatus(200)
                                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("["))

        );

        assertThatThrownBy(() -> countryGateway.retrieveCurrencyByCountryCode(CountryCode.FR))
                .isInstanceOf(TechnicalException.class)
                .hasMessage("Failed to parse JSON response.")
                .hasCauseInstanceOf(JsonProcessingException.class);
    }

}
