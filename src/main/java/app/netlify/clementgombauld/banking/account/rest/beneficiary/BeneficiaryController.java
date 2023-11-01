package app.netlify.clementgombauld.banking.account.rest.beneficiary;

import app.netlify.clementgombauld.banking.account.rest.beneficiary.in.AddBeneficiaryRequest;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.in.DeleteBeneficiaryRequest;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.out.AddBeneficiaryResponse;
import app.netlify.clementgombauld.banking.account.usecases.AddBeneficiary;
import app.netlify.clementgombauld.banking.account.usecases.DeleteBeneficiary;
import app.netlify.clementgombauld.banking.account.usecases.commands.AddBeneficiaryCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/beneficiary")
public class BeneficiaryController {

    private final ObjectMapper objectMapper;

    private final AddBeneficiary addBeneficiary;

    private final DeleteBeneficiary deleteBeneficiary;

    @Autowired
    public BeneficiaryController(ObjectMapper objectMapper, AddBeneficiary addBeneficiary, DeleteBeneficiary deleteBeneficiary) {
        this.objectMapper = objectMapper;
        this.addBeneficiary = addBeneficiary;
        this.deleteBeneficiary = deleteBeneficiary;
    }

    @PostMapping
    public ResponseEntity<AddBeneficiaryResponse> addBeneficiary(@RequestBody AddBeneficiaryRequest request) {
        AddBeneficiaryCommand command = objectMapper.convertValue(request, AddBeneficiaryCommand.class);
        String beneficiaryId = addBeneficiary.handle(command);
        return new ResponseEntity<>(new AddBeneficiaryResponse(beneficiaryId), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBeneficiary(@RequestBody DeleteBeneficiaryRequest request) {
        deleteBeneficiary.handle(request.beneficiaryIban());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
