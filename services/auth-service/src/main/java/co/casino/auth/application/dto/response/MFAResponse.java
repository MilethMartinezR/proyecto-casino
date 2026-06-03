package co.casino.auth.application.dto.response;

public class MFAResponse {
    private String secret;
    private String qrCodeUrl;

    public MFAResponse(String secret, String qrCodeUrl) {
        this.secret = secret;
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getSecret() { return secret; }
    public String getQrCodeUrl() { return qrCodeUrl; }
}