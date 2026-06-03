package co.casino.auth.domain.model;

import java.time.Instant;

public class Session {
    private String id;
    private String userId;
    private String token;
    private String ipAddress;
    private String userAgent;
    private boolean active;
    private Instant createdAt;
    private Instant expiresAt;

    public Session() {}

    public Session(String id, String userId, String token, String ipAddress,
                   String userAgent, boolean active, Instant createdAt, Instant expiresAt) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.active = active;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}