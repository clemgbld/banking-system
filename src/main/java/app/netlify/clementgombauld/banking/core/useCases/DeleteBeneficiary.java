package app.netlify.clementgombauld.banking.core.useCases;


import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;
import app.netlify.clementgombauld.banking.core.domain.exceptions.UnknownAccountWithIbanException;

public class DeleteBeneficiary {
    private final AccountRepository accountRepository;
    public DeleteBeneficiary(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void handle(String accountIban, String beneficiaryIban) {
        Account account = accountRepository.findByIban(accountIban)
                .orElseThrow(()-> {
                    throw new UnknownAccountWithIbanException(accountIban);
                });
        account.deleteBeneficiaryByIban(beneficiaryIban);
        accountRepository.update(account);
    }
}
