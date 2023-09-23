package app.netlify.clementgombauld.banking.core.domain;

public interface ExtraBankTransactionsGateway {
    void transfer(MoneyTransferred transaction,String iban,String bic);
}
