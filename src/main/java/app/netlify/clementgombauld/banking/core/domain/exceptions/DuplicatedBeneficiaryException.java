package app.netlify.clementgombauld.banking.core.domain.exceptions;

public class DuplicatedBeneficiaryException extends RuntimeException{
    public DuplicatedBeneficiaryException(String beneficiaryIban, String accountId) {
        super(String.format("The beneficiary with the iban : %s is already a beneficiary of the account %s",beneficiaryIban,accountId));
    }
}
