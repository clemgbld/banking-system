package app.netlify.clementgombauld.banking.account.rest.account.out;

import java.math.BigDecimal;
import java.util.List;

public record AccountOverviewDto(String firstName, String lastName, String accountNumber, BigDecimal balance,
                                 List<TransactionDto> transactions) {
}
