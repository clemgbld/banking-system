package app.netlify.clementgombauld.banking.account.usecases.commands;

import java.math.BigDecimal;

public record TransferMoneyCommand(BigDecimal transactionAmount, String receiverAccountIdentifier, String bic,
                                   String reason) {
}
