package app.netlify.clementgombauld.banking.account.integration;

import app.netlify.clementgombauld.banking.account.domain.BankInfoType;
import app.netlify.clementgombauld.banking.account.domain.Currency;
import app.netlify.clementgombauld.banking.account.domain.CurrencyGateway;
import app.netlify.clementgombauld.banking.account.infra.currencygateway.RestCurrencyGateway;
import app.netlify.clementgombauld.banking.account.infra.exceptions.TechnicalException;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class RestCurrencyGatewayIT extends WireMockTestClass {

    private CurrencyGateway currencyGateway;

    private static final String FAKE_API_KEY = "fake_api_key";

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        String currencyBaseUrl = wireMockServer.baseUrl();
        currencyGateway = new RestCurrencyGateway(currencyBaseUrl, FAKE_API_KEY, restTemplate);
    }

    @Test
    void shouldRetrieveExchangeRate() {
        wireMockServer.stubFor(
                WireMock.get("/latest.json?app_id=" + FAKE_API_KEY + "&base=USD")
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("""
                                        {
                                          "disclaimer": "Usage subject to terms: https://openexchangerates.org/terms",
                                          "license": "https://openexchangerates.org/license",
                                          "timestamp": 1697360402,
                                          "base": "USD",
                                          "rates": {
                                            "AED": 3.673,
                                            "AFN": 75.718177,
                                            "ALL": 100.584954,
                                            "AMD": 398.155003,
                                            "ANG": 1.803015,
                                            "AOA": 825.5261,
                                            "ARS": 349.930056,
                                            "AUD": 1.590515,
                                            "AWG": 1.8,
                                            "AZN": 1.7,
                                            "BAM": 1.860275,
                                            "BBD": 2,
                                            "BDT": 110.296307,
                                            "BGN": 1.86051,
                                            "BHD": 0.377134,
                                            "BIF": 2837.247246,
                                            "BMD": 1,
                                            "BND": 1.369849,
                                            "BOB": 6.913017,
                                            "BRL": 5.0807,
                                            "BSD": 1,
                                            "BTC": 0.000037164311,
                                            "BTN": 83.281697,
                                            "BWP": 13.742331,
                                            "BYN": 3.295477,
                                            "BZD": 2.01655,
                                            "CAD": 1.36685,
                                            "CDF": 2482.048449,
                                            "CHF": 0.904157,
                                            "CLF": 0.033982,
                                            "CLP": 938.403005,
                                            "CNH": 7.30683,
                                            "CNY": 7.3043,
                                            "COP": 4270.0338,
                                            "CRC": 531.091533,
                                            "CUC": 1,
                                            "CUP": 25.75,
                                            "CVE": 104.857255,
                                            "CZK": 23.4951,
                                            "DJF": 178.123366,
                                            "DKK": 7.096255,
                                            "DOP": 56.834282,
                                            "DZD": 137.886,
                                            "EGP": 30.880297,
                                            "ERN": 15,
                                            "ETB": 55.788432,
                                            "EUR": 0.951249,
                                            "FJD": 2.28685,
                                            "FKP": 0.82481,
                                            "GBP": 0.82481,
                                            "GEL": 2.67625,
                                            "GGP": 0.82481,
                                            "GHS": 11.760118,
                                            "GIP": 0.82481,
                                            "GMD": 65.75,
                                            "GNF": 8587.398759,
                                            "GTQ": 7.846103,
                                            "GYD": 209.302338,
                                            "HKD": 7.8236,
                                            "HNL": 24.669256,
                                            "HRK": 7.1696,
                                            "HTG": 134.503423,
                                            "HUF": 369.001685,
                                            "IDR": 15703,
                                            "ILS": 3.978123,
                                            "IMP": 0.82481,
                                            "INR": 83.279549,
                                            "IQD": 1311.553194,
                                            "IRR": 42250,
                                            "ISK": 139.201949,
                                            "JEP": 0.82481,
                                            "JMD": 155.276553,
                                            "JOD": 0.7096,
                                            "JPY": 149.55999466,
                                            "KES": 147.79094,
                                            "KGS": 89.17,
                                            "KHR": 4129.133087,
                                            "KMF": 466.850286,
                                            "KPW": 900,
                                            "KRW": 1353.065,
                                            "KWD": 0.312075,
                                            "KYD": 0.83369,
                                            "KZT": 477.025024,
                                            "LAK": 20672.468224,
                                            "LBP": 15036.5166,
                                            "LKR": 324.154532,
                                            "LRD": 186.850009,
                                            "LSL": 19.01907,
                                            "LYD": 4.877539,
                                            "MAD": 10.212869,
                                            "MDL": 18.000271,
                                            "MGA": 4512.256836,
                                            "MKD": 58.605603,
                                            "MMK": 2100.917046,
                                            "MNT": 3450,
                                            "MOP": 8.063442,
                                            "MRU": 38.209917,
                                            "MUR": 44.32,
                                            "MVR": 15.35,
                                            "MWK": 1159.937142,
                                            "MXN": 18.0913,
                                            "MYR": 4.7285,
                                            "MZN": 63.850001,
                                            "NAD": 18.97,
                                            "NGN": 775.859771,
                                            "NIO": 36.594584,
                                            "NOK": 10.876712,
                                            "NPR": 133.189769,
                                            "NZD": 1.698659,
                                            "OMR": 0.385124,
                                            "PAB": 1,
                                            "PEN": 3.842868,
                                            "PGK": 3.692735,
                                            "PHP": 56.809498,
                                            "PKR": 277.568721,
                                            "PLN": 4.31335,
                                            "PYG": 7348.592894,
                                            "QAR": 3.650069,
                                            "RON": 4.7246,
                                            "RSD": 111.468089,
                                            "RUB": 97.700004,
                                            "RWF": 1219.574855,
                                            "SAR": 3.751398,
                                            "SBD": 8.433052,
                                            "SCR": 13.262567,
                                            "SDG": 600.5,
                                            "SEK": 10.95198,
                                            "SGD": 1.3705,
                                            "SHP": 0.82481,
                                            "SLL": 20969.5,
                                            "SOS": 571.740587,
                                            "SRD": 38.253,
                                            "SSP": 130.26,
                                            "STD": 22281.8,
                                            "STN": 23.293513,
                                            "SVC": 8.753507,
                                            "SYP": 2512.53,
                                            "SZL": 19.000333,
                                            "THB": 36.298484,
                                            "TJS": 10.98968,
                                            "TMT": 3.5,
                                            "TND": 3.1765,
                                            "TOP": 2.397162,
                                            "TRY": 27.8008,
                                            "TTD": 6.791268,
                                            "TWD": 32.204901,
                                            "TZS": 2504.119724,
                                            "UAH": 36.380558,
                                            "UGX": 3751.362023,
                                            "USD": 1,
                                            "UYU": 39.767922,
                                            "UZS": 12233.897839,
                                            "VES": 34.856499,
                                            "VND": 24452.876237,
                                            "VUV": 118.722,
                                            "WST": 2.7185,
                                            "XAF": 623.978134,
                                            "XAG": 0.04402766,
                                            "XAU": 0.00051743,
                                            "XCD": 2.70255,
                                            "XDR": 0.760831,
                                            "XOF": 623.978134,
                                            "XPD": 0.00086985,
                                            "XPF": 113.514145,
                                            "XPT": 0.00113557,
                                            "YER": 250.300053,
                                            "ZAR": 19.0073,
                                            "ZMW": 21.334475,
                                            "ZWL": 322
                                          }
                                        }"""))
        );


        Currency initialCurrency = new Currency("USD");
        BankInfoType targetCurrency = BankInfoType.CURRENCY;
        BigDecimal expectedExchangeRate = new BigDecimal("0.9512490000000000112123643702943809330463409423828125");

        Optional<BigDecimal> actualExchangeRate = currencyGateway.retrieveExchangeRate(initialCurrency, targetCurrency);

        assertThat(actualExchangeRate).isPresent();
        assertThat(actualExchangeRate.get()).isEqualTo(expectedExchangeRate);
    }

    @Test
    void shouldThrowATechnicalExceptionWhenTheJsonParsingFail() {
        wireMockServer.stubFor(
                WireMock.get("/latest.json?app_id=" + FAKE_API_KEY + "&base=USD")
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("[]")
                        ));

        Currency initialCurrency = new Currency("USD");
        BankInfoType targetCurrency = BankInfoType.CURRENCY;

        assertThatThrownBy(() -> currencyGateway.retrieveExchangeRate(initialCurrency, targetCurrency)).isInstanceOf(TechnicalException.class)
                .hasMessage("Failed to parse JSON response.")
                .hasCauseInstanceOf(RestClientException.class);
    }


}
