package app.netlify.clementgombauld.banking.identityaccess.rest.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @Min(value = 2, message = "First name should have minimum length of 2 characters.") String firstName,
        @Min(value = 2, message = "Last name should have minimum length of 2 characters.") String lastName,
        @Email(message = "Email should be valid.") String email,
        @NotBlank(message = "Password is required") String password
) {
}
