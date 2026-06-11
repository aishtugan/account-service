package account.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordRequest(
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 12, message = "Password length must be 12 chars minimum!")
        String new_password
) {
}
