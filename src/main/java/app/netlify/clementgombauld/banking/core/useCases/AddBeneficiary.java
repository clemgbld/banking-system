package app.netlify.clementgombauld.banking.core.useCases;

import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.core.domain.IdGenerator;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownAccountWithIbanException;


public class AddBeneficiary {
    private final AccountRepository accountRepository;

    private final IdGenerator idGenerator;
    public AddBeneficiary(AccountRepository accountRepository,IdGenerator idGenerator) {
        this.accountRepository = accountRepository;
        this.idGenerator = idGenerator;
    }

    public String handle(String accountIban, String beneficiaryIban, String beneficiaryBic, String beneficiaryName) {
        Account account = accountRepository.findByIban(accountIban)
                .orElseThrow(()-> {
                    throw new UnknownAccountWithIbanException(accountIban);
                });
        String beneficiaryId = idGenerator.generate();
        account.addBeneficiary(beneficiaryId,beneficiaryIban,beneficiaryBic,beneficiaryName);
        accountRepository.update(account);
        return beneficiaryId;
    }
}
