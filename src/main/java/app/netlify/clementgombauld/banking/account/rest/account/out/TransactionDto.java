package app.netlify.clementgombauld.banking.account.rest.account.out;

import java.math.BigDecimal;
import java.time.Instant;


public record TransactionDto(String id, String accountName, long creationDate, BigDecimal transactionAmount,
                             String reason) {

    public TransactionDto(String id, String accountName, Instant creationDate, BigDecimal transactionAmount, String reason) {
        this(id, accountName, creationDate.toEpochMilli(), transactionAmount, reason);
    }

}
