package app.netlify.clementgombauld.banking.account.rest.account.in;

import java.math.BigDecimal;

public record ReceiveMoneyFromExternalBankRequest(String receiverAccountIban, String senderAccountIdentifier,
                                                  String senderAccountBic, String senderAccountName,
                                                  BigDecimal transactionAmount, String reason) {
}
