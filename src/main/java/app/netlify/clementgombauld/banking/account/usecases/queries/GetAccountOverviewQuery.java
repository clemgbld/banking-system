package app.netlify.clementgombauld.banking.account.usecases.queries;

import java.util.Objects;

public record GetAccountOverviewQuery(String customerId, int limit) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GetAccountOverviewQuery that)) return false;
        return limit == that.limit && Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, limit);
    }

    @Override
    public String toString() {
        return "AccountQuery{" +
                "customerId='" + customerId + '\'' +
                ", limit=" + limit +
                '}';
    }
}
