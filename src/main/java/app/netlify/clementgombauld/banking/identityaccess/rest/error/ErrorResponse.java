package app.netlify.clementgombauld.banking.identityaccess.rest.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public record ErrorResponse(String message, int httpStatus) {
}
