package app.netlify.clementgombauld.banking.core.domain;

public interface ExternalBankTransactionsGateway {
    void transfer(Transaction transaction, String iban, String bic);
}
