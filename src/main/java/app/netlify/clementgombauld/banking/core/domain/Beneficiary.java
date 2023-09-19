package app.netlify.clementgombauld.banking.core.domain;

import app.netlify.clementgombauld.banking.core.domain.exceptions.InvalidIbanException;
import org.iban4j.Iban;

public class Beneficiary {
    private final String id;

    private final Iban iban;

    private final String bic;

    private final String name;

    public Beneficiary(String id, String iban, String bic,String name) {
        this.id = id;
        try {
            this.iban = Iban.valueOf(iban);
        }catch (Exception e){
            throw new InvalidIbanException();
        }

        this.bic = bic;
        this.name = name;
    }

    public String getIban() {
        return iban.toString();
    }

    public String getName(){
        return name;
    }


    boolean hasIban(String iban){
       return getIban().equals(iban);
    }

    public boolean isInDifferentBank(String bic) {
        return !this.bic.equals(bic);
    }
}
