package app.netlify.clementgombauld.banking.account.rest.account.out;

import java.math.BigDecimal;


public record TransactionDto(String id, String accountName, long creationDate, BigDecimal transactionAmount,
                             String reason) {

}
