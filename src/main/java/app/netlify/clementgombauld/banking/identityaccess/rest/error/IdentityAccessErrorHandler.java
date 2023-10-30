package app.netlify.clementgombauld.banking.identityaccess.rest.error;

import app.netlify.clementgombauld.banking.common.rest.error.IllegalArgumentExceptionHandler;
import app.netlify.clementgombauld.banking.identityaccess.rest.IdentityAccessController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = {IdentityAccessController.class})
public class IdentityAccessErrorHandler extends ResponseEntityExceptionHandler implements IllegalArgumentExceptionHandler {
}
