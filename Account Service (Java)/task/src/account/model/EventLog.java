package account.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "event_log")
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private EventAction action;

    @Column(nullable = false)
    private String subject;

    private String object;
    private String path;

    public EventLog() {
    }

    public EventLog(EventAction action, String subject, String object, String path) {
        this.date = LocalDateTime.now();
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public Long getId() {
        return id;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public EventAction getAction() {
        return action;
    }
    public String getSubject() {
        return subject;
    }
    public String getObject() {
        return object;
    }
    public String getPath() {
        return path;
    }

}
