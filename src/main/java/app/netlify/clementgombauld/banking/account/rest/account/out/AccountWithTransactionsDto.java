package app.netlify.clementgombauld.banking.account.rest.account.out;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record AccountWithTransactionsDto(String iban, BigDecimal balance, List<TransactionDto> transactionsDto) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountWithTransactionsDto that)) return false;
        return Objects.equals(iban, that.iban) && Objects.equals(balance, that.balance) && Objects.equals(transactionsDto, that.transactionsDto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iban, balance, transactionsDto);
    }

    @Override
    public String toString() {
        return "AccountWithTransactionsDto{" +
                "iban='" + iban + '\'' +
                ", balance=" + balance +
                ", transactionsDto=" + transactionsDto +
                '}';
    }
}
