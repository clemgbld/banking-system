package app.netlify.clementgombauld.banking.core.domain;


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


    boolean hasIban(String iban) {
        return this.iban.equals(new Iban(iban));
    }

    public boolean isInDifferentBank(String bic) {
        return !this.bic.equals(new Bic(bic));
    }


}
