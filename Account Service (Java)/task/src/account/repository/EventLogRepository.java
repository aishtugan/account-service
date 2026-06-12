package account.repository;

import account.model.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {

    Optional<EventLog> findBySubjectAndObjectAndPath(String subject, String object, String path);
}
