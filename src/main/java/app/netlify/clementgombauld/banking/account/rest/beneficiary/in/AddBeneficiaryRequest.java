package app.netlify.clementgombauld.banking.account.rest.beneficiary.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record AddBeneficiaryRequest(
        @Valid
        @NotBlank(message = "Beneficiary iban is required") String beneficiaryIban,
        @Valid
        @NotBlank(message = "Beneficiary iban is required") String beneficiaryBic,
        @Valid
        @NotBlank(message = "Beneficiary name is required") String beneficiaryName) {
}
