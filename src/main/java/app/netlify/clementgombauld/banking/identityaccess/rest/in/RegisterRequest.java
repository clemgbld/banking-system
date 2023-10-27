package app.netlify.clementgombauld.banking.identityaccess.rest.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @Pattern(regexp = "^[a-zA-Z-]+$", message = "First Name can only contain letters and hyphens") String firstName,
        @Pattern(regexp = "^[a-zA-Z-]+$", message = "Last Name can only contain letters and hyphens") String lastName,
        @Email(message = "Email should be valid.") String email,
        @NotBlank(message = "Password is required") String password
) {
}
