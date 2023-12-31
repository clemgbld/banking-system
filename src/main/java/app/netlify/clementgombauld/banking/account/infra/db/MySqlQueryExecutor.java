package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.rest.account.out.PageDto;
import app.netlify.clementgombauld.banking.account.usecases.queries.QueryExecutor;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.TransactionDto;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.out.BeneficiaryDto;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverviewQuery;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetTransactionsQuery;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.iban4j.Iban;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

import static app.netlify.clementgombauld.banking.account.infra.db.entity.QJpaAccountEntity.jpaAccountEntity;
import static app.netlify.clementgombauld.banking.account.infra.db.entity.QJpaTransactionEntity.jpaTransactionEntity;
import static app.netlify.clementgombauld.banking.account.infra.db.entity.QJpaBeneficiaryEntity.jpaBeneficiaryEntity;


@Repository
public class MySqlQueryExecutor implements QueryExecutor {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public MySqlQueryExecutor(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<AccountWithTransactionsDto> findAccountWithTransactionsByCustomerId(GetAccountOverviewQuery query) {
        List<AccountWithTransactionsDto> accountWithTransactionsDtoList = queryFactory
                .select(Projections.constructor(AccountWithTransactionsDto.class,
                        jpaAccountEntity.iban,
                        jpaAccountEntity.balance,
                        Projections.list(Projections.constructor(TransactionDto.class,
                                jpaTransactionEntity.id,
                                jpaTransactionEntity.accountName,
                                jpaTransactionEntity.creationDate,
                                jpaTransactionEntity.transactionAmount,
                                jpaTransactionEntity.reason
                        ))
                ))
                .from(jpaAccountEntity)
                .leftJoin(jpaAccountEntity.transactions, jpaTransactionEntity)
                .on(jpaTransactionEntity.account.eq(jpaAccountEntity))
                .where(jpaAccountEntity.customerId.eq(query.customerId()))
                .orderBy(jpaTransactionEntity.creationDate.desc())
                .limit(query.limit())
                .fetch();

        return accountWithTransactionsDtoList
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Iban> findIbanByCustomerId(String customerId) {

        List<String> ibanList = queryFactory.select(Projections.constructor(String.class, jpaAccountEntity.iban))
                .from(jpaAccountEntity)
                .where(jpaAccountEntity.customerId.eq(customerId))
                .fetch();

        return ibanList
                .stream()
                .findFirst()
                .map(Iban::valueOf);
    }

    @Override
    public List<BeneficiaryDto> findBeneficiariesByCustomerId(String customerId) {
        return queryFactory.select(
                        Projections.constructor(
                                BeneficiaryDto.class,
                                jpaBeneficiaryEntity.id,
                                jpaBeneficiaryEntity.iban,
                                jpaBeneficiaryEntity.bic,
                                jpaBeneficiaryEntity.name
                        )
                ).from(jpaBeneficiaryEntity)
                .where(
                        jpaBeneficiaryEntity.account.customerId.eq(customerId)
                ).orderBy(jpaBeneficiaryEntity.name.asc())
                .fetch();
    }

    @Override
    public PageDto<TransactionDto> findTransactionsByCustomerId(GetTransactionsQuery query) {

        Pageable pageable = PageRequest.of(query.pageNumber(), query.pageSize(), Sort.by("accountName").ascending());

        List<TransactionDto> transactions = queryFactory.select(Projections.constructor(
                        TransactionDto.class,
                        jpaTransactionEntity.id,
                        jpaTransactionEntity.accountName,
                        jpaTransactionEntity.creationDate,
                        jpaTransactionEntity.transactionAmount,
                        jpaTransactionEntity.reason
                ))
                .from(jpaTransactionEntity)
                .where(jpaTransactionEntity.account.customerId.eq(query.customerId()))
                .orderBy(jpaTransactionEntity.accountName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        long totalCount = queryFactory
                .from(jpaTransactionEntity)
                .where(jpaTransactionEntity.account.customerId.eq(query.customerId()))
                .fetch()
                .size();

        Page<TransactionDto> page = PageableExecutionUtils.getPage(transactions, pageable, () -> totalCount);

        return new PageDto<>(transactions, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
