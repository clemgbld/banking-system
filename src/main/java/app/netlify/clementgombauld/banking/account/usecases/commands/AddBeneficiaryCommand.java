package app.netlify.clementgombauld.banking.account.usecases.commands;

public record AddBeneficiaryCommand(String beneficiaryIban, String beneficiaryBic, String beneficiaryName) {
}
