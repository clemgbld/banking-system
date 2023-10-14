package app.netlify.clementgombauld.banking.account.infra.currencygateway;

import app.netlify.clementgombauld.banking.account.domain.BankInfoType;
import app.netlify.clementgombauld.banking.account.domain.Currency;
import app.netlify.clementgombauld.banking.account.domain.CurrencyGateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class RestCurrencyGateway implements CurrencyGateway {
    @Override
    public Optional<BigDecimal> retrieveExchangeRate(Currency initialCurrency, BankInfoType targetCurrency) {
        return Optional.empty();
    }
}
