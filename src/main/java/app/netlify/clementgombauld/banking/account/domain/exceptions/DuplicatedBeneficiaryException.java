package app.netlify.clementgombauld.banking.account.domain.exceptions;

public class DuplicatedBeneficiaryException extends IllegalArgumentException {
    public DuplicatedBeneficiaryException(String beneficiaryIban, String accountId) {
        super(String.format("The beneficiary with the accountIdentifier : %s is already a beneficiary of the account %s", beneficiaryIban, accountId));
    }
}
