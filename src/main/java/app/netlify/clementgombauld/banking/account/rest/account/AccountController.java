package app.netlify.clementgombauld.banking.account.rest.account;

import app.netlify.clementgombauld.banking.account.rest.account.in.CloseAccountRequest;
import app.netlify.clementgombauld.banking.account.rest.account.in.ReceiveMoneyFromExternalBankRequest;
import app.netlify.clementgombauld.banking.account.rest.account.in.TransferMoneyRequest;
import app.netlify.clementgombauld.banking.account.usecases.CloseAccount;
import app.netlify.clementgombauld.banking.account.usecases.OpenAccount;
import app.netlify.clementgombauld.banking.account.usecases.ReceiveMoneyFromExternalBank;
import app.netlify.clementgombauld.banking.account.usecases.TransferMoney;
import app.netlify.clementgombauld.banking.account.usecases.commands.CloseAccountCommand;
import app.netlify.clementgombauld.banking.account.usecases.commands.ReceiveMoneyFromExternalBankCommand;
import app.netlify.clementgombauld.banking.account.usecases.commands.TransferMoneyCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
    private final OpenAccount openAccount;

    private final CloseAccount closeAccount;

    private final TransferMoney transferMoney;

    private final ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank;

    @Autowired
    public AccountController(OpenAccount openAccount, CloseAccount closeAccount, TransferMoney transferMoney, ReceiveMoneyFromExternalBank receiveMoneyFromExternalBank) {
        this.openAccount = openAccount;
        this.closeAccount = closeAccount;
        this.transferMoney = transferMoney;
        this.receiveMoneyFromExternalBank = receiveMoneyFromExternalBank;
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

}
