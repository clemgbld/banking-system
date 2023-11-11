package app.netlify.clementgombauld.banking.account.usecases.queries;

import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.PageDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.TransactionDto;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.out.BeneficiaryDto;

import org.iban4j.Iban;


import java.util.List;
import java.util.Optional;

public interface QueryExecutor {

    Optional<AccountWithTransactionsDto> findAccountWithTransactionsByCustomerId(GetAccountOverviewQuery query);

    Optional<Iban> findIbanByCustomerId(String customerId);

    List<BeneficiaryDto> findBeneficiariesByCustomerId(String customerId);

    PageDto<TransactionDto> findTransactionsByCustomerId(GetTransactionsQuery query);
}
