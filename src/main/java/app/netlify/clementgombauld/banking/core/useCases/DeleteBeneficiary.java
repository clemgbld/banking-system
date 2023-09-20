package app.netlify.clementgombauld.banking.core.useCases;


import app.netlify.clementgombauld.banking.core.domain.Account;
import app.netlify.clementgombauld.banking.core.domain.AccountRepository;

public class DeleteBeneficiary {
    private final AccountRepository accountRepository;
    public DeleteBeneficiary(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void handle(String accountIban, String beneficiaryIban) {
        Account account = accountRepository.findByIban(accountIban)
                .orElseThrow(RuntimeException::new);
        account.deleteBeneficiaryByIban(beneficiaryIban);
        accountRepository.update(account);
    }
}
