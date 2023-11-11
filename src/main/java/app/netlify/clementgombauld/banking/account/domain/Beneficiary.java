package app.netlify.clementgombauld.banking.account.domain;


import app.netlify.clementgombauld.banking.account.domain.exceptions.SameIbanThanAccountException;

import java.util.Objects;

public class Beneficiary {

    private final String id;

    private final Iban iban;

    private final Bic bic;

    private final String name;

    public Beneficiary(String id, Iban iban, Bic bic, String name, Iban accountIban) {
        checkThatIbanIsNotAccountIban(iban, accountIban);
        this.id = id;
        this.iban = iban;
        this.bic = bic;
        this.name = name;
    }

    public String getId() {
        return id;
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


    public boolean isInDifferentBank(Bic bic) {
        return !this.bic.equals(bic);
    }

    private void checkThatIbanIsNotAccountIban(Iban benficiaryIban, Iban accountIban) {
        if (benficiaryIban.equals(accountIban)) {
            throw new SameIbanThanAccountException();
        }
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
