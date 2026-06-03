package co.casino.auth.application.dto.response;

public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String role;
    private boolean mfaEnabled;

    public UserResponse(String id, String username, String email, String role, boolean mfaEnabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.mfaEnabled = mfaEnabled;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isMfaEnabled() { return mfaEnabled; }
}