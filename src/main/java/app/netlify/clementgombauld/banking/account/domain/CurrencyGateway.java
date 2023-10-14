package app.netlify.clementgombauld.banking.account.domain;

import java.math.BigDecimal;
import java.util.Optional;

public interface CurrencyGateway {
    Optional<BigDecimal> retrieveExchangeRate(Currency initialCurrency, BankInfoType targetCurrency);
}
