package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.core.domain.IdGenerator;

public class AddBeneficiary {
    private final AccountRepository accountRepository;

    private final IdGenerator idGenerator;
    public AddBeneficiary(AccountRepository accountRepository,IdGenerator idGenerator) {
        this.accountRepository = accountRepository;
        this.idGenerator = idGenerator;
    }

    public void handle(String accountId, String beneficiaryIban, String beneficiaryBic, String beneficiaryName) {
        Account account = accountRepository.findById(accountId).orElseThrow(RuntimeException::new);
        account.addBeneficiary(idGenerator.generate(),beneficiaryIban,beneficiaryBic,beneficiaryName);
        accountRepository.update(account);
    }
}
