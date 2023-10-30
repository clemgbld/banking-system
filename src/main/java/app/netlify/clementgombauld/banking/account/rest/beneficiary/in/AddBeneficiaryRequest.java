package app.netlify.clementgombauld.banking.account.rest.beneficiary.in;

public record AddBeneficiaryRequest(String beneficiaryIban, String beneficiaryBic, String beneficiaryName) {
}
