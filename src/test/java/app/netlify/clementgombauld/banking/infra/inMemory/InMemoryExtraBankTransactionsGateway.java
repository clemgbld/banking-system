package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.account_bc.core.domain.ExtraBankTransactionsGateway;
import app.netlify.clementgombauld.banking.account_bc.core.domain.MoneyTransferred;

import java.util.List;

public class InMemoryExtraBankTransactionsGateway implements ExtraBankTransactionsGateway {
    private final List<MoneyTransferred> extraBankTransactions;

    public InMemoryExtraBankTransactionsGateway(List<MoneyTransferred> extraBankTransactions) {
        this.extraBankTransactions = extraBankTransactions;
    }

    @Override
    public void transfer(MoneyTransferred transaction) {
        extraBankTransactions.add(transaction);
    }
}
