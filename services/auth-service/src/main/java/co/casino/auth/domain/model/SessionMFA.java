package co.casino.auth.domain.model;

import java.time.Instant;

public class SessionMFA {
    private String id;
    private String userId;
    private String tempToken;   // token de corta vida previo al MFA
    private boolean verified;
    private Instant createdAt;
    private Instant expiresAt;

    public SessionMFA() {}

    public SessionMFA(String id, String userId, String tempToken,
                      boolean verified, Instant createdAt, Instant expiresAt) {
        this.id = id;
        this.userId = userId;
        this.tempToken = tempToken;
        this.verified = verified;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTempToken() { return tempToken; }
    public void setTempToken(String tempToken) { this.tempToken = tempToken; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}