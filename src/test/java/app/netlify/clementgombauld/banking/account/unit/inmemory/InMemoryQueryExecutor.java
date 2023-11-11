package app.netlify.clementgombauld.banking.account.unit.inmemory;

import app.netlify.clementgombauld.banking.account.rest.account.out.PageDto;
import app.netlify.clementgombauld.banking.account.usecases.queries.QueryExecutor;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.TransactionDto;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.out.BeneficiaryDto;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverviewQuery;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetTransactionsQuery;
import org.iban4j.Iban;


import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryQueryExecutor implements QueryExecutor {
    private final Map<Object, Object> store;

    public InMemoryQueryExecutor(Map<Object, Object> store) {
        this.store = store;
    }

    @Override
    public Optional<AccountWithTransactionsDto> findAccountWithTransactionsByCustomerId(GetAccountOverviewQuery query) {
        return Optional.ofNullable((AccountWithTransactionsDto) store.get(query));
    }

    @Override
    public Optional<Iban> findIbanByCustomerId(String customerId) {
        return Optional.ofNullable((Iban) store.get(customerId));
    }

    @Override
    public List<BeneficiaryDto> findBeneficiariesByCustomerId(String customerId) {
        return (List<BeneficiaryDto>) store.get(customerId);
    }

    @Override
    public PageDto<TransactionDto> findTransactionsByCustomerId(GetTransactionsQuery query) {
        List<TransactionDto> transactions = (List<TransactionDto>) store.get(query);
        return new PageDto<>(transactions, query.pageNumber(), query.pageSize(), transactions.size(), 1);
    }
}
