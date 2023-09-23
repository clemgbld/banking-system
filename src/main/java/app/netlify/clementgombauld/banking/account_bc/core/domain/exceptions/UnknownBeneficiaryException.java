package app.netlify.clementgombauld.banking.account_bc.core.domain.exceptions;

public class UnknownBeneficiaryException extends RuntimeException{

    public UnknownBeneficiaryException(String iban) {
        super(String.format("Cannot find any account with the iban: %s in your beneficiaries list.",iban));
    }
}
