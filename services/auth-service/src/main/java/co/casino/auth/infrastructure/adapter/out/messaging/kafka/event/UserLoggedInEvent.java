package co.casino.auth.infrastructure.adapter.out.messaging.kafka.event;

import java.time.Instant;

public class UserLoggedInEvent {
    private String userId;
    private String email;
    private Instant occurredAt;

    public UserLoggedInEvent(String userId, String email) {
        this.userId = userId;
        this.email = email;
        this.occurredAt = Instant.now();
    }

    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public Instant getOccurredAt() { return occurredAt; }
}