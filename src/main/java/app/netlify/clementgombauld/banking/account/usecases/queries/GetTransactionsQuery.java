package app.netlify.clementgombauld.banking.account.usecases.queries;

public record GetTransactionsQuery(String customerId, int pageNumber, int pageSize) {
}
