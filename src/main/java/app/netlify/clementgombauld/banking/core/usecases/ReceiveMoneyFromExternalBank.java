package app.netlify.clementgombauld.banking.core.usecases;

import app.netlify.clementgombauld.banking.core.domain.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public class ReceiveMoneyFromExternalBank {

    private final AccountRepository accountRepository;

    private final IdGenerator idGenerator;

    private final DateProvider dateProvider;

    private final CurrencyByCountryCodeGateway currencyByCountryCodeGateway;

    private final ExchangeRateGateway exchangeRateGateway;

    public ReceiveMoneyFromExternalBank(AccountRepository accountRepository, IdGenerator idGenerator, DateProvider dateProvider, CurrencyByCountryCodeGateway currencyByCountryCodeGateway, ExchangeRateGateway exchangeRateGateway) {

        this.accountRepository = accountRepository;
        this.idGenerator = idGenerator;
        this.dateProvider = dateProvider;
        this.currencyByCountryCodeGateway = currencyByCountryCodeGateway;
        this.exchangeRateGateway = exchangeRateGateway;
    }


    public void handle(String receiverAccountIban, String senderAccountIban, String senderAccountBic, String senderAccountName, BigDecimal transactionAmount) {
        Account receiverAccount = accountRepository.findByIban(receiverAccountIban).orElseThrow(RuntimeException::new);
        String transactionId = idGenerator.generate();
        Instant currentDate = dateProvider.now();
        if ("FR".equals(senderAccountIban.substring(0, 2))) {
            receiverAccount.deposit(transactionId, currentDate, transactionAmount, senderAccountIban, senderAccountBic, senderAccountName);
            accountRepository.update(receiverAccount);
            return;
        }

        String currency = currencyByCountryCodeGateway.retrieve(senderAccountIban.substring(0, 2)).orElseThrow();

        BigDecimal exchangeRate = exchangeRateGateway.retrieve(currency, "EUR").orElseThrow();

        receiverAccount.deposit(transactionId, currentDate, transactionAmount.multiply(exchangeRate), senderAccountIban, senderAccountBic, senderAccountName);

        accountRepository.update(receiverAccount);

    }
}
