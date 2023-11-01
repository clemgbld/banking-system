package app.netlify.clementgombauld.banking.account.rest.account.error;

import app.netlify.clementgombauld.banking.account.rest.account.AccountController;
import app.netlify.clementgombauld.banking.common.rest.error.IllegalArgumentExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = {AccountController.class})
public class AccountErrorHandler extends ResponseEntityExceptionHandler implements IllegalArgumentExceptionHandler {
}
