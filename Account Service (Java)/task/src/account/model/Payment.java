package account.model;

import jakarta.persistence.*;

import java.time.Period;
import java.time.YearMonth;

@Entity
@Table(name = "payments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "period"})
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private YearMonth period;
    private Long salary;

    public Payment() {
    }

    public Payment(User user, YearMonth period, Long salary) {
        this.user = user;
        this.period = period;
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }
    public YearMonth getPeriod() {
        return period;
    }
    public Long getSalary() {
        return salary;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public void setPeriod(YearMonth period) {
        this.period = period;
    }
    public void setSalary(Long salary) {
        this.salary = salary;
    }
}
