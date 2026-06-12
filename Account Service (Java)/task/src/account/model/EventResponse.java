package account.model;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        LocalDateTime date,
        String action,
        String subject,
        String object,
        String path
) {
}
