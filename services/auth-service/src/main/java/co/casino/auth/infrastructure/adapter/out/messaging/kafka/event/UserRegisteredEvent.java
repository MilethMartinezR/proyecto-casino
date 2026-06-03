package co.casino.auth.infrastructure.adapter.out.messaging.kafka.event;

import java.time.Instant;

public class UserRegisteredEvent {
    private String userId;
    private String username;
    private String email;
    private String role;
    private Instant occurredAt;

    public UserRegisteredEvent(String userId, String username, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.occurredAt = Instant.now();
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public Instant getOccurredAt() { return occurredAt; }
}