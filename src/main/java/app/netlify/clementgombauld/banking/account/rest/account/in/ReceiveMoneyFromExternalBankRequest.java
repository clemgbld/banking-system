package app.netlify.clementgombauld.banking.account.rest.account.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ReceiveMoneyFromExternalBankRequest(@Valid
                                                  @NotBlank(message = "Receiver account iban is required") String receiverAccountIban,
                                                  @Valid
                                                  @NotBlank(message = "Sender account identifier is required") String senderAccountIdentifier,
                                                  @Valid
                                                  @NotBlank(message = "Sender account bic is required") String senderAccountBic,
                                                  String senderAccountName,
                                                  @Valid
                                                  @DecimalMin(value = "0.01", message = "Amount must be greater than 0") BigDecimal transactionAmount,
                                                  String reason) {
}
