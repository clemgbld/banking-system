package app.netlify.clementgombauld.banking.account.rest.account.error;

import app.netlify.clementgombauld.banking.account.infra.exceptions.TechnicalException;
import app.netlify.clementgombauld.banking.account.rest.account.AccountController;
import app.netlify.clementgombauld.banking.common.rest.error.ErrorResponse;
import app.netlify.clementgombauld.banking.common.rest.error.IllegalArgumentExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = {AccountController.class})
public class AccountErrorHandler extends ResponseEntityExceptionHandler implements IllegalArgumentExceptionHandler {
    @ExceptionHandler(TechnicalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseEntity<ErrorResponse> handleTechnicalException(TechnicalException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
