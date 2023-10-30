package app.netlify.clementgombauld.banking.account.rest.beneficiary.in;

import jakarta.validation.constraints.NotBlank;

public record AddBeneficiaryRequest(@NotBlank(message = "Beneficiary iban is required") String beneficiaryIban,
                                    @NotBlank(message = "Beneficiary iban is required") String beneficiaryBic,
                                    @NotBlank(message = "Beneficiary name is required") String beneficiaryName) {
}
