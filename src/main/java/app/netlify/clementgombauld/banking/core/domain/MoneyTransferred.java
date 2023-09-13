package app.netlify.clementgombauld.banking.core.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record MoneyTransferred(String id, Instant creationDate, BigDecimal transactionAmount, String iban, String accountName) {
}
