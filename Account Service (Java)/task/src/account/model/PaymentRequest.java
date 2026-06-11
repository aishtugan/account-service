package account.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PaymentRequest(
        @NotBlank(message = "Employee cannot be blank")
        @Pattern(
                regexp = "^[\\w.-]+@acme.com",
                message = "Employee must be in the format ***@acme.com"
        )
        String employee,

        @NotBlank(message = "Period cannot be blank")
        @Pattern(regexp = "^(0[1-9]|1[0-2])-\\d{4}$", message = "Wrong date!")
        String period,

        @Min(value = 0, message = "Salary cannot be negative!")
        Long salary
) {
}
