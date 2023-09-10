package app.netlify.clementgombauld.banking.core.domain;

public class Beneficiary {
    private final String id;

    private final String iban;

    private final String bic;

    private final String firstName;

    private final String lastName;

    public Beneficiary(String id, String iban, String bic, String firstName, String lastName) {
        this.id = id;
        this.iban = iban;
        this.bic = bic;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getIban() {
        return iban;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
