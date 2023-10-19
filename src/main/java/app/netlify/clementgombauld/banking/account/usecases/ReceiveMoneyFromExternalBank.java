package app.netlify.clementgombauld.banking.account.usecases;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.domain.exceptions.SameBankException;
import app.netlify.clementgombauld.banking.account.domain.exceptions.UnknownAccountWithIbanException;
import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import app.netlify.clementgombauld.banking.common.domain.IdGenerator;

import java.math.BigDecimal;
import java.time.Instant;


public class ReceiveMoneyFromExternalBank {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final BeneficiaryRepository beneficiaryRepository;

    private final IdGenerator idGenerator;

    private final DateProvider dateProvider;

    private final CurrencyConverter currencyConverter;

    public ReceiveMoneyFromExternalBank(AccountRepository accountRepository, TransactionRepository transactionRepository, BeneficiaryRepository beneficiaryRepository, IdGenerator idGenerator, DateProvider dateProvider, CountryGateway countryGateway, CurrencyGateway currencyGateway) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.idGenerator = idGenerator;
        this.dateProvider = dateProvider;
        this.currencyConverter = new CurrencyConverter(countryGateway, currencyGateway);
    }


    public void handle(String receiverAccountIban, String senderAccountIdentifier, String senderAccountBic, String senderAccountName, BigDecimal transactionAmount, String bic) {
        Bic validSenderAccountBic = new Bic(senderAccountBic);
        Bic bankBic = new Bic(bic);
        if (bankBic.equals(validSenderAccountBic)) {
            throw new SameBankException();
        }
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban)
                .orElseThrow(() -> new UnknownAccountWithIbanException(receiverAccountIban));
        String transactionId = idGenerator.generate();
        Instant currentDate = dateProvider.now();
        if (validSenderAccountBic.isBankCountry()) {
            receiverAccount.deposit(transactionAmount);
            accountRepository.update(receiverAccount);
            String accountName = beneficiaryRepository
                    .findByAccountIdAndIban(receiverAccount.getId(), senderAccountIdentifier)
                    .map(Beneficiary::getName)
                    .orElse(senderAccountName);
            transactionRepository.insert(receiverAccount.getId(), receiverAccount.recordDepositTransaction(transactionId, currentDate, transactionAmount, senderAccountIdentifier, validSenderAccountBic, accountName));
            return;
        }

        BigDecimal convertedAmount = currencyConverter.convert(validSenderAccountBic, transactionAmount);
        receiverAccount.deposit(convertedAmount);
        accountRepository.update(receiverAccount);
        transactionRepository.insert(receiverAccount.getId(), new Transaction(transactionId, currentDate, convertedAmount, senderAccountIdentifier, senderAccountBic, senderAccountName));

    }
}
