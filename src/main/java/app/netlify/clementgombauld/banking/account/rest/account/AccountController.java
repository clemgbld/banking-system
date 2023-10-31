package app.netlify.clementgombauld.banking.account.rest.account;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    @PostMapping("/open")
    public ResponseEntity<Void> openAccount() {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
