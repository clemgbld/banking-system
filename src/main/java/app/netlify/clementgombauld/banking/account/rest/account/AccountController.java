package app.netlify.clementgombauld.banking.account.rest.account;

import app.netlify.clementgombauld.banking.account.usecases.OpenAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final OpenAccount openAccount;

    @Autowired
    public AccountController(OpenAccount openAccount) {
        this.openAccount = openAccount;
    }

    @PostMapping("/open")
    public ResponseEntity<Void> openAccount() {
        openAccount.handle();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
