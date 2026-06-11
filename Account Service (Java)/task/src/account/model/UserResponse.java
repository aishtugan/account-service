package account.model;

public record UserResponse(
        Long id,
        String name,
        String lastname,
        String email
) {
}
