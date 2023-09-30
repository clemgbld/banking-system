package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;

import java.math.BigDecimal;
import java.time.Instant;


public class ReceiveMoneyFromExternalBank {

    private final AccountRepository accountRepository;

    private final IdGenerator idGenerator;

    private final DateProvider dateProvider;

    private final CurrencyConverter currencyConverter;

    public ReceiveMoneyFromExternalBank(AccountRepository accountRepository, IdGenerator idGenerator, DateProvider dateProvider, CountryGateway countryGateway, CurrencyGateway currencyGateway) {
        this.accountRepository = accountRepository;
        this.idGenerator = idGenerator;
        this.dateProvider = dateProvider;
        this.currencyConverter = new CurrencyConverter(countryGateway, currencyGateway);
    }


    public void handle(String receiverAccountIban, String senderAccountIdentifier, String senderAccountBic, String senderAccountName, BigDecimal transactionAmount) {
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);
        String transactionId = idGenerator.generate();
        Instant currentDate = dateProvider.now();
        Bic validSenderAccountBic = new Bic(senderAccountBic);
        if (validSenderAccountBic.isFrench()) {
            receiverAccount.deposit(transactionId, currentDate, transactionAmount, senderAccountIdentifier, senderAccountBic, senderAccountName);
            accountRepository.update(receiverAccount);
            return;
        }

        BigDecimal convertedAmount = currencyConverter.convert(validSenderAccountBic, transactionAmount);

        receiverAccount.deposit(transactionId, currentDate, convertedAmount, senderAccountIdentifier, senderAccountBic, senderAccountName);

        accountRepository.update(receiverAccount);

    }
}
