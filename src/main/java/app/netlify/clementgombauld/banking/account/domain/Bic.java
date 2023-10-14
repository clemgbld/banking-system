package app.netlify.clementgombauld.banking.account.domain;

import app.netlify.clementgombauld.banking.account.domain.exceptions.InvalidBicException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.NoBicException;
import org.iban4j.CountryCode;
;

import java.util.Objects;
import java.util.Optional;

public class Bic {

    private final org.iban4j.Bic bic;

    private final CountryCode countryCode;

    public Bic(String value) {
        this.bic = validateBic(value);
        this.countryCode = this.bic.getCountryCode();
    }

    public String value() {
        return bic.toString();
    }

    private org.iban4j.Bic validateBic(String value) {
        String nonNullValue = Optional.ofNullable(value)
                .orElseThrow(NoBicException::new);
        try {
            return org.iban4j.Bic.valueOf(nonNullValue);
        } catch (Exception exception) {
            throw new InvalidBicException(nonNullValue);
        }
    }


    public CountryCode getCountryCode() {
        return countryCode;
    }

    public boolean isBankCountry() {
        return getCountryCode().equals(CountryCode.FR);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bic bic1)) return false;
        return Objects.equals(bic, bic1.bic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bic);
    }

    @Override
    public String toString() {
        return "Bic{" +
                "bic=" + bic +
                '}';
    }

}
