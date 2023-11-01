package app.netlify.clementgombauld.banking.account.rest.account;

import app.netlify.clementgombauld.banking.account.rest.account.in.CloseAccountRequest;
import app.netlify.clementgombauld.banking.account.usecases.CloseAccount;
import app.netlify.clementgombauld.banking.account.usecases.OpenAccount;
import app.netlify.clementgombauld.banking.account.usecases.commands.CloseAccountCommand;
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

    @Autowired
    public AccountController(OpenAccount openAccount, CloseAccount closeAccount) {
        this.openAccount = openAccount;
        this.closeAccount = closeAccount;
    }

    @PostMapping("/open")
    public ResponseEntity<Void> openAccount() {
        openAccount.handle();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/close")
    public ResponseEntity<Void> closeAccount(@RequestBody CloseAccountRequest request, @Value("${bic}") String bic) {
        CloseAccountCommand closeAccountCommand = new CloseAccountCommand(request.externalAccountIban(), request.externalBic(), bic, request.accountName());
        closeAccount.handle(closeAccountCommand);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
