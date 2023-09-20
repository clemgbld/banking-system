package app.netlify.clementgombauld.banking.core.domain;



public class Beneficiary {

    private final Iban iban;

    private final Bic bic;

    private final String name;

    public Beneficiary(String id, String iban, String bic,String name) {
        this.iban = new Iban(iban);
        this.bic = new Bic(bic);

        this.name = name;
    }

    public String getIban() {
        return iban.value();
    }

    public String getName(){
        return name;
    }


    boolean hasIban(String iban){
       return this.iban.equals(new Iban(iban));
    }

    public boolean isInDifferentBank(String bic) {
        return !this.bic.equals(new Bic(bic));
    }
}
