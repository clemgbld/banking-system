package app.netlify.clementgombauld.banking.account.infra.db;

import app.netlify.clementgombauld.banking.account.domain.QueryExecutor;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountWithTransactionsDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.TransactionDto;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverviewQuery;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

import static app.netlify.clementgombauld.banking.account.infra.db.entity.QJpaAccountEntity.jpaAccountEntity;
import static app.netlify.clementgombauld.banking.account.infra.db.entity.QJpaTransactionEntity.jpaTransactionEntity;


@Repository
public class MySqlQueryExecutor implements QueryExecutor {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public MySqlQueryExecutor(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<AccountWithTransactionsDto> getAccountWithTransactions(GetAccountOverviewQuery query) {

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
}
