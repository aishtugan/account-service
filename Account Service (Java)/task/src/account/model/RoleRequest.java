package account.model;

import jakarta.validation.constraints.NotNull;

public record RoleRequest(
        @NotNull
        String user,

        @NotNull
        String role,

        @NotNull
        RoleOperation operation
) {
}
