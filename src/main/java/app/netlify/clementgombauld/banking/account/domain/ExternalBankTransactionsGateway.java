package app.netlify.clementgombauld.banking.account.domain;

public interface ExternalBankTransactionsGateway {
    void transfer(Transaction transaction, String iban, String bic);
}
