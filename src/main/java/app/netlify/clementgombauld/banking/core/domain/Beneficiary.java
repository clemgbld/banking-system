package app.netlify.clementgombauld.banking.core.domain;

public class Beneficiary {
    private final String id;

    private final String iban;

    private final String bic;

    private final String name;

    public Beneficiary(String id, String iban, String bic,String name) {
        this.id = id;
        this.iban = iban;
        this.bic = bic;
        this.name = name;
    }

    public String getIban() {
        return iban;
    }

    public String getName(){
        return name;
    }


    boolean hasIban(String iban){
       return this.iban.equals(iban);
    }
}
