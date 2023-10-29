package app.netlify.clementgombauld.banking.account.infra;

import app.netlify.clementgombauld.banking.account.domain.ExternalBankTransactionsGateway;
import app.netlify.clementgombauld.banking.account.domain.Transaction;
import org.springframework.stereotype.Component;

// would be a secondary adapter to be able to do the link with another bounded context
@Component
public class FakeExternalBankTransactionsGateway implements ExternalBankTransactionsGateway {
    @Override
    public void transfer(Transaction transaction, String iban, String bic) {
    }
}
