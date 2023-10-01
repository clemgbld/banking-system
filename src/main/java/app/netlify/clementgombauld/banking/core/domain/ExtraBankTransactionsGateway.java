package app.netlify.clementgombauld.banking.core.domain;

public interface ExtraBankTransactionsGateway {
    void transfer(Transaction transaction, String iban, String bic);
}
