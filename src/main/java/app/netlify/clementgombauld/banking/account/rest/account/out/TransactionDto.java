package app.netlify.clementgombauld.banking.account.rest.account.out;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionDto(String accountName, long creationDate, BigDecimal transactionAmount, String reason) {
}
