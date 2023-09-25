package app.netlify.clementgombauld.banking.core.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record MoneyTransferred(String id, Instant creationDate, BigDecimal transactionAmount, String iban, String bic,
                               String accountName) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoneyTransferred that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(creationDate, that.creationDate) && Objects.equals(transactionAmount, that.transactionAmount) && Objects.equals(iban, that.iban) && Objects.equals(bic, that.bic) && Objects.equals(accountName, that.accountName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDate, transactionAmount, iban, bic, accountName);
    }

    @Override
    public String toString() {
        return "MoneyTransferred{" +
                "id='" + id + '\'' +
                ", creationDate=" + creationDate +
                ", transactionAmount=" + transactionAmount +
                ", iban='" + iban + '\'' +
                ", bic='" + bic + '\'' +
                ", accountName='" + accountName + '\'' +
                '}';
    }
}
