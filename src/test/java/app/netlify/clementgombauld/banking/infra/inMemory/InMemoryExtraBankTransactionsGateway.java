package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.ExtraBankTransactionsGateway;
import app.netlify.clementgombauld.banking.core.domain.MoneyTransferred;

import java.util.List;

public class InMemoryExtraBankTransactionsGateway implements ExtraBankTransactionsGateway {
    private final List<MoneyTransferred> extraBankTransactions;

    private final List<String> accountInfos;

    public InMemoryExtraBankTransactionsGateway(List<MoneyTransferred> extraBankTransactions, List<String> accountInfos) {
        this.extraBankTransactions = extraBankTransactions;
        this.accountInfos = accountInfos;
    }

    @Override
    public void transfer(MoneyTransferred transaction,String iban,String bic) {
        extraBankTransactions.add(transaction);
        accountInfos.add(iban);
        accountInfos.add(bic);
    }
}
