package app.netlify.clementgombauld.banking.account.rest.beneficiary.in;

import jakarta.validation.constraints.NotBlank;

public record DeleteBeneficiaryRequest(@NotBlank(message = "Beneficiary iban is required") String beneficiaryIban) {
}
