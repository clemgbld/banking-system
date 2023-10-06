package app.netlify.clementgombauld.banking.infra;

import app.netlify.clementgombauld.banking.core.domain.ExternalBankTransactionsGateway;
import app.netlify.clementgombauld.banking.core.domain.Transaction;
import org.springframework.stereotype.Component;

@Component
public class FakeExternalBankTransactionsGateway implements ExternalBankTransactionsGateway {
    @Override
    public void transfer(Transaction transaction, String iban, String bic) {
        
    }
}
