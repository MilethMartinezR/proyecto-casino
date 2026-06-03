package co.casino.auth.application.dto.response;

public class SessionResponse {
    private String token;
    private String userId;
    private boolean mfaRequired;
    private String tempToken;   // presente solo si mfaRequired=true

    public SessionResponse(String token, String userId, boolean mfaRequired, String tempToken) {
        this.token = token;
        this.userId = userId;
        this.mfaRequired = mfaRequired;
        this.tempToken = tempToken;
    }

    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public boolean isMfaRequired() { return mfaRequired; }
    public String getTempToken() { return tempToken; }
}