package app.netlify.clementgombauld.banking.core.domain;


import app.netlify.clementgombauld.banking.core.domain.exceptions.InvalidBicException;
import org.iban4j.Bic;

public class Beneficiary {

    private final Iban iban;

    private final Bic bic;

    private final String name;

    public Beneficiary(String id, String iban, String bic,String name) {
        this.iban = new Iban(iban);
        try {
            this.bic = Bic.valueOf(bic);
        }catch (Exception e){
            throw new InvalidBicException();
        }

        this.name = name;
    }

    public String getIban() {
        return iban.value();
    }

    public String getName(){
        return name;
    }


    boolean hasIban(String iban){
       return this.iban.isSameIban(iban);
    }

    public boolean isInDifferentBank(String bic) {
        return !this.bic.toString().equals(bic);
    }
}
