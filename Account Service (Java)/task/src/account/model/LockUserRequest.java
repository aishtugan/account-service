package account.model;

public record LockUserRequest(
        String user,
        LockOperation operation
) {
}
