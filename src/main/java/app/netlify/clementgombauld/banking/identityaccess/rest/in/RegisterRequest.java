package app.netlify.clementgombauld.banking.identityaccess.rest.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @Valid
        @Pattern(regexp = "^[a-zA-Z-]+$", message = "First Name can only contain letters and hyphens") String firstName,
        @Valid
        @Pattern(regexp = "^[a-zA-Z-]+$", message = "Last Name can only contain letters and hyphens") String lastName,
        @Valid
        @Email(message = "Email should be valid.") String email,
        @Valid
        @NotBlank(message = "Password is required") String password
) {
}
