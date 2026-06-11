package account.repository;

import account.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByUserEmailIgnoreCaseAndPeriod(String email, YearMonth period);

    Optional<Payment> findByUserEmailIgnoreCaseAndPeriod(String email, YearMonth period);

    List<Payment> findAllByUserEmailIgnoreCaseOrderByPeriodDesc(String email);
}
