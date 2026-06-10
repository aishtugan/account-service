package account.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRequest(

        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotBlank(message = "Lastname cannot be blank")
        String lastname,

        @NotBlank(message = "Email cannot be blank")
        @Pattern(
                regexp = "^[\\w.-]+@acme.com",
                message = "Email must be in the format ***@acme.com"
        )
        String email,

        @NotBlank(message = "Password cannot be blank")
        String password
) {}
