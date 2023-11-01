package app.netlify.clementgombauld.banking.account.rest.account.in;

import java.math.BigDecimal;

public record TransferMoneyRequest(BigDecimal transactionAmount, String receiverAccountIdentifier,
                                   String reason) {
}
