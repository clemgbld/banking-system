package app.netlify.clementgombauld.banking.account.unit.inmemory;

import app.netlify.clementgombauld.banking.account.usecases.queries.QueryExecutor;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.TransactionDto;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.out.BeneficiaryDto;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverviewQuery;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetTransactionsQuery;
import org.iban4j.Iban;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
    public Page<TransactionDto> findTransactionsByCustomerId(GetTransactionsQuery query) {
        return new PageImpl<>((List<TransactionDto>) store.get(query), PageRequest.of(query.pageNumber(), query.pageSize(), Sort.by("creationDate").descending()), 1L);
    }
}
