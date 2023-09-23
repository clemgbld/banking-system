package app.netlify.clementgombauld.banking.account.core.domain;

public interface ExtraBankTransactionsGateway {
    void transfer(MoneyTransferred transaction);
}
