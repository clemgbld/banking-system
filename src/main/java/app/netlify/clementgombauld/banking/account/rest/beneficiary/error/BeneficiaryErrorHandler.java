package app.netlify.clementgombauld.banking.account.rest.beneficiary.error;

import app.netlify.clementgombauld.banking.account.rest.beneficiary.BeneficiaryController;
import app.netlify.clementgombauld.banking.common.rest.error.IllegalArgumentExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = {BeneficiaryController.class})
public class BeneficiaryErrorHandler extends ResponseEntityExceptionHandler implements IllegalArgumentExceptionHandler {

}
