package app.netlify.clementgombauld.banking.account.rest.account;

import app.netlify.clementgombauld.banking.account.rest.account.in.CloseAccountRequest;
import app.netlify.clementgombauld.banking.account.rest.account.in.ReceiveMoneyFromExternalBankRequest;
import app.netlify.clementgombauld.banking.account.rest.account.in.TransferMoneyRequest;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountDetailsDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.AccountOverviewDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.PageDto;
import app.netlify.clementgombauld.banking.account.rest.account.out.TransactionDto;
import app.netlify.clementgombauld.banking.account.usecases.commands.CloseAccount;
import app.netlify.clementgombauld.banking.account.usecases.commands.OpenAccount;
import app.netlify.clementgombauld.banking.account.usecases.commands.ReceiveMoneyFromExternalBank;
import app.netlify.clementgombauld.banking.account.usecases.commands.TransferMoney;
import app.netlify.clementgombauld.banking.account.usecases.commands.CloseAccountCommand;
import app.netlify.clementgombauld.banking.account.usecases.commands.ReceiveMoneyFromExternalBankCommand;
import app.netlify.clementgombauld.banking.account.usecases.commands.TransferMoneyCommand;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountDetails;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetAccountOverview;
import app.netlify.clementgombauld.banking.account.usecases.queries.GetTransactions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
    private final OpenAccount openAccount;

    private final CloseAccount closeAccount;

    private final TransferMoney transferMoney;

    private final ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank;

    private final GetAccountOverview getAccountOverview;

    private final GetAccountDetails getAccountDetails;

    private final GetTransactions getTransactions;

    @Autowired
    public AccountController(OpenAccount openAccount, CloseAccount closeAccount, TransferMoney transferMoney, ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank, GetAccountOverview getAccountOverview, GetAccountDetails getAccountDetails, GetTransactions getTransactions) {
        this.openAccount = openAccount;
        this.closeAccount = closeAccount;
        this.transferMoney = transferMoney;
        this.receiveMoneyFromExternalBank = receiveMoneyFromExternalBank;
        this.getAccountOverview = getAccountOverview;
        this.getAccountDetails = getAccountDetails;
        this.getTransactions = getTransactions;
    }

    @PostMapping("/open")
    public ResponseEntity<Void> openAccount() {
        openAccount.handle();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/close")
    public ResponseEntity<Void> closeAccount(@RequestBody CloseAccountRequest request, @Value("${bic}") String bic) {
        CloseAccountCommand command = new CloseAccountCommand(request.externalAccountIban(), request.externalBic(), bic, request.accountName());
        closeAccount.handle(command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferMoney(@RequestBody TransferMoneyRequest request, @Value("${bic}") String bic) {
        TransferMoneyCommand command = new TransferMoneyCommand(request.transactionAmount(), request.receiverAccountIdentifier(), bic, request.reason());
        transferMoney.handle(command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/receive")
    public ResponseEntity<Void> receiveMoneyFromExternalBank(@RequestBody ReceiveMoneyFromExternalBankRequest request, @Value("${bic}") String bic) {
        ReceiveMoneyFromExternalBankCommand command = new ReceiveMoneyFromExternalBankCommand(request.receiverAccountIban(), request.senderAccountIdentifier(), request.senderAccountBic(), request.senderAccountName(), request.transactionAmount(), bic, request.reason());
        receiveMoneyFromExternalBank.handle(command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/overview")
    public ResponseEntity<AccountOverviewDto> getAccountOverview(@RequestParam(required = false) Integer limit) {
        AccountOverviewDto accountOverview = getAccountOverview.handle(limit);
        return ResponseEntity.ok(accountOverview);
    }

    @GetMapping("/details")
    public ResponseEntity<AccountDetailsDto> getAccountDetails(@Value("${bic}") String bic) {
        AccountDetailsDto accountDetails = getAccountDetails.handle(bic);
        return ResponseEntity.ok(accountDetails);
    }

    @GetMapping("/transactions")
    public ResponseEntity<PageDto<TransactionDto>> getTransactions(@RequestParam(required = false) Integer pageNumber, @RequestParam(required = false) Integer pageSize) {
        PageDto<TransactionDto> transactionPage = getTransactions.handle(pageNumber, pageSize);
        return ResponseEntity.ok(transactionPage);
    }

}
