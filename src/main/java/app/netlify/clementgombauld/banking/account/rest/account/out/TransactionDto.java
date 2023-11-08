package app.netlify.clementgombauld.banking.account.rest.account.out;

import java.math.BigDecimal;
import java.util.Objects;


public record TransactionDto(String id, String accountName, long creationDate, BigDecimal transactionAmount,
                             String reason) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionDto that)) return false;
        return creationDate == that.creationDate && Objects.equals(accountName, that.accountName) && Objects.equals(transactionAmount, that.transactionAmount) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountName, creationDate, transactionAmount, reason);
    }

    @Override
    public String toString() {
        return "TransactionDto{" +
                "accountName='" + accountName + '\'' +
                ", creationDate=" + creationDate +
                ", transactionAmount=" + transactionAmount +
                ", reason='" + reason + '\'' +
                '}';
    }
}
