package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class UnknownBeneficiaryException extends IllegalArgumentException {

    public UnknownBeneficiaryException(String iban) {
        super(String.format("Cannot find any account with the accountIdentifier: %s in your beneficiaries list.", iban));
    }
}
