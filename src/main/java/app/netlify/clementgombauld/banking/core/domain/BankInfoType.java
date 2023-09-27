package app.netlify.clementgombauld.banking.core.domain;

public enum BankInfoType {
    COUNTRY("FR"),
    CURRENCY("EUR");

    private final String value;

    BankInfoType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
