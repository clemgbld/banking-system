package app.netlify.clementgombauld.banking.account.usecases.commands;

import java.math.BigDecimal;

public record ReceiveMoneyFromExternalBankCommand(String receiverAccountIban, String senderAccountIdentifier,
                                                  String senderAccountBic, String senderAccountName,
                                                  BigDecimal transactionAmount, String bic, String reason) {
}
