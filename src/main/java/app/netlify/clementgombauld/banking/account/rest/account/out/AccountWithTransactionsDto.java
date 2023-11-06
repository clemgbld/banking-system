package app.netlify.clementgombauld.banking.account.rest.account.out;

import java.math.BigDecimal;
import java.util.List;

public record AccountWithTransactionsDto(String iban, BigDecimal balance, List<TransactionDto> transactionsDto) {
}
