package app.netlify.clementgombauld.banking.account.rest.beneficiary;

import app.netlify.clementgombauld.banking.account.rest.beneficiary.in.AddBeneficiaryRequest;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.out.AddBeneficiaryResponse;
import app.netlify.clementgombauld.banking.account.usecases.AddBeneficiary;
import app.netlify.clementgombauld.banking.account.usecases.commands.AddBeneficiaryCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/beneficiary")
public class BeneficiaryController {

    private final ObjectMapper objectMapper;

    private final AddBeneficiary addBeneficiary;

    @Autowired
    public BeneficiaryController(ObjectMapper objectMapper, AddBeneficiary addBeneficiary) {
        this.objectMapper = objectMapper;
        this.addBeneficiary = addBeneficiary;
    }

    @PostMapping
    public ResponseEntity<AddBeneficiaryResponse> addBeneficiary(@RequestBody AddBeneficiaryRequest request) {
        AddBeneficiaryCommand addBeneficiaryCommand = objectMapper.convertValue(request, AddBeneficiaryCommand.class);
        String beneficiaryId = addBeneficiary.handle(addBeneficiaryCommand);
        return new ResponseEntity<>(new AddBeneficiaryResponse(beneficiaryId), HttpStatus.CREATED);
    }

}
