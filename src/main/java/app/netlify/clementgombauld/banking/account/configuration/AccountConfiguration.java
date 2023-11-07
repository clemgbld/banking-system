package app.netlify.clementgombauld.banking.account.configuration;

import app.netlify.clementgombauld.banking.account.domain.*;
import app.netlify.clementgombauld.banking.account.usecases.commands.*;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverview;
import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import app.netlify.clementgombauld.banking.common.domain.IdGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AccountConfiguration {
    @Bean
    public RestTemplate buildRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public AddBeneficiary addBeneficiary(BeneficiaryRepository beneficiaryRepository, IdGenerator idGenerator, AuthenticationGateway authenticationGateway, AccountRepository accountRepository) {
        return new AddBeneficiary(beneficiaryRepository, idGenerator, authenticationGateway, accountRepository);
    }

    @Bean
    public CloseAccount closeAccount(AccountRepository accountRepository, AuthenticationGateway authenticationGateway, ExternalBankTransactionsGateway externalBankTransactionsGateway, DateProvider dateProvider, IdGenerator idGenerator, TransactionRepository transactionRepository) {
        return new CloseAccount(accountRepository, authenticationGateway, externalBankTransactionsGateway, dateProvider, idGenerator, transactionRepository);
    }

    @Bean
    public DeleteBeneficiary deleteBeneficiary(BeneficiaryRepository beneficiaryRepository, AuthenticationGateway authenticationGateway, AccountRepository accountRepository) {
        return new DeleteBeneficiary(beneficiaryRepository, authenticationGateway, accountRepository);
    }

    @Bean
    public OpenAccount openAccount(AccountRepository accountRepository, IbanGenerator ibanGenerator, IdGenerator idGenerator, AuthenticationGateway authenticationGateway, DateProvider dateProvider) {
        return new OpenAccount(accountRepository, ibanGenerator, idGenerator, authenticationGateway, dateProvider);
    }

    @Bean
    public ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank(AccountRepository accountRepository, TransactionRepository transactionRepository, BeneficiaryRepository beneficiaryRepository, IdGenerator idGenerator, DateProvider dateProvider, CountryGateway countryGateway, CurrencyGateway currencyGateway) {
        return new ReceiveMoneyFromExternalBank(accountRepository, transactionRepository, beneficiaryRepository, idGenerator, dateProvider, countryGateway, currencyGateway);
    }

    @Bean
    public TransferMoney transferMoney(AccountRepository accountRepository, BeneficiaryRepository beneficiaryRepository, TransactionRepository transactionRepository, DateProvider dateProvider, IdGenerator idGenerator, ExternalBankTransactionsGateway externalBankTransactionsGateway, AuthenticationGateway authenticationGateway) {
        return new TransferMoney(accountRepository, beneficiaryRepository, transactionRepository, dateProvider, idGenerator, externalBankTransactionsGateway, authenticationGateway);
    }

    @Bean
    public GetAccountOverview getAccountOverview(AuthenticationGateway authenticationGateway, QueryExecutor queryExecutor) {
        return new GetAccountOverview(authenticationGateway, queryExecutor);
    }


}
