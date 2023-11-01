package app.netlify.clementgombauld.banking.account.rest.account.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record TransferMoneyRequest(@Valid
                                   @DecimalMin(value = "0.01", message = "Amount must be greater than 0") BigDecimal transactionAmount,
                                   @Valid
                                   @NotBlank(message = "Receiver account identifier required") String receiverAccountIdentifier,
                                   String reason) {
}
