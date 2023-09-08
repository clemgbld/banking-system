package app.netlify.clementgombauld.banking.core.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(String id, Instant creationDate, BigDecimal transactionAmount,String iban) {
}
