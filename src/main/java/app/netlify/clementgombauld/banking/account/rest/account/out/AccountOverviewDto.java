package app.netlify.clementgombauld.banking.account.rest.account.out;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record AccountOverviewDto(String firstName, String lastName, String accountNumber, BigDecimal balance,
                                 List<TransactionDto> transactions) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountOverviewDto that)) return false;
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(accountNumber, that.accountNumber) && Objects.equals(balance, that.balance) && Objects.equals(transactions, that.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, accountNumber, balance, transactions);
    }

    @Override
    public String toString() {
        return "AccountOverviewDto{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", transactions=" + transactions +
                '}';
    }
}
