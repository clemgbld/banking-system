package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;

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


    public void handle(String receiverAccountIban, String senderAccountIdentifier, String senderAccountBic, String senderAccountName, BigDecimal transactionAmount) {
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);
        String transactionId = idGenerator.generate();
        Instant currentDate = dateProvider.now();
        Bic validSenderAccountBic = new Bic(senderAccountBic);
        if (validSenderAccountBic.isBankCountry()) {
            receiverAccount.deposit(transactionAmount);
            accountRepository.update(receiverAccount);
            String accountName = beneficiaryRepository
                    .findByAccountIdAndIban(receiverAccount.getId(), senderAccountIdentifier)
                    .map(Beneficiary::getName)
                    .orElse(senderAccountName);
            transactionRepository.insert(receiverAccount.getId(), new Transaction(transactionId, currentDate, transactionAmount, senderAccountIdentifier, senderAccountBic, accountName));
            return;
        }

        BigDecimal convertedAmount = currencyConverter.convert(validSenderAccountBic, transactionAmount);
        receiverAccount.deposit(convertedAmount);
        accountRepository.update(receiverAccount);
        transactionRepository.insert(receiverAccount.getId(), new Transaction(transactionId, currentDate, convertedAmount, senderAccountIdentifier, senderAccountBic, senderAccountName));

    }
}
