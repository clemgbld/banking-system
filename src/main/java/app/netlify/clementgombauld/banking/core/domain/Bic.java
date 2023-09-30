package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.InvalidBicException;

import java.util.Objects;

public class Bic {

    private final org.iban4j.Bic bic;

    private final org.iban4j.CountryCode countryCode;

    public Bic(String value) {
        this.bic = validateBic(value);
        this.countryCode = this.bic.getCountryCode();
    }

    public String value() {
        return bic.toString();
    }

    private org.iban4j.Bic validateBic(String value) {
        try {
            return org.iban4j.Bic.valueOf(value);
        } catch (Exception exception) {
            throw new InvalidBicException(value);
        }
    }


    public String getCountryCode() {
        return countryCode.name();
    }

    public boolean isBankCountry() {
        return getCountryCode().equals(BankInfoType.COUNTRY.getValue());
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
