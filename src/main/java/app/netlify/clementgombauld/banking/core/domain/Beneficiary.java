package app.netlify.clementgombauld.banking.core.domain;


import java.util.Objects;

public class Beneficiary {

    private final String id;

    private final Iban iban;

    private final Bic bic;

    private final String name;

    public Beneficiary(String id, String iban, String bic, String name) {
        this.id = id;
        this.iban = new Iban(iban);
        this.bic = new Bic(bic);

        this.name = name;
    }

    public String getIban() {
        return iban.value();
    }

    public String getName() {
        return name;
    }

    public String getBic() {
        return bic.value();
    }


    boolean hasIban(String iban) {
        return this.iban.equals(new Iban(iban));
    }

    public boolean isInDifferentBank(Bic bic) {
        return !this.bic.equals(bic);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Beneficiary that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(iban, that.iban) && Objects.equals(bic, that.bic) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, iban, bic, name);
    }

    @Override
    public String toString() {
        return "Beneficiary{" +
                "id='" + id + '\'' +
                ", accountIdentifier=" + iban +
                ", bic=" + bic +
                ", name='" + name + '\'' +
                '}';
    }
}
