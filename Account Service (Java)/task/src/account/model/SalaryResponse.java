package account.model;

import java.time.YearMonth;

public record SalaryResponse(
        String name,
        String lastname,
        String period,
        String salary) {
}
