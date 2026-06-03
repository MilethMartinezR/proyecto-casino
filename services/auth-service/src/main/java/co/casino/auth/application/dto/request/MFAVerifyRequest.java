package co.casino.auth.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class MFAVerifyRequest {

    @NotBlank(message = "El token temporal es obligatorio")
    @JsonAlias("tempCode")   // frontend envía tempCode, backend usa tempToken
    private String tempToken;

    @NotBlank(message = "El código MFA es obligatorio")
    @JsonAlias("mfaCode")    // frontend envía mfaCode, backend usa code
    private String code;

    public String getTempToken() { return tempToken; }
    public void setTempToken(String tempToken) { this.tempToken = tempToken; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
