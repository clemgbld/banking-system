package app.netlify.clementgombauld.banking.account.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record Transaction(String id, Instant creationDate, BigDecimal transactionAmount, String accountIdentifier,
                          String bic,
                          String accountName, String reason) {

}
