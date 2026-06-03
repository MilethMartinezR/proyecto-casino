package co.casino.auth.infrastructure.adapter.in.rest;

import co.casino.auth.application.dto.request.AuthRequest;
import co.casino.auth.application.dto.request.MFAVerifyRequest;
import co.casino.auth.application.dto.response.ApiResponse;
import co.casino.auth.application.dto.response.SessionResponse;
import co.casino.auth.application.dto.response.UserResponse;
import co.casino.auth.domain.port.in.LoginUseCase;
import co.casino.auth.domain.port.in.LogoutUseCase;
import co.casino.auth.domain.port.in.MfaVerifyUseCase;
import co.casino.auth.domain.port.in.RegisterUserUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final MfaVerifyUseCase mfaVerifyUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase,
                          MfaVerifyUseCase mfaVerifyUseCase,
                          LogoutUseCase logoutUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.mfaVerifyUseCase = mfaVerifyUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody AuthRequest request) {
        UserResponse user = registerUserUseCase.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Usuario registrado correctamente", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<SessionResponse>> login(@Valid @RequestBody AuthRequest request,
                                                               HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        String ua = httpRequest.getHeader("User-Agent");
        SessionResponse session = loginUseCase.login(request, ip, ua);
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", session));
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<ApiResponse<SessionResponse>> verifyMfa(@Valid @RequestBody MFAVerifyRequest request) {
        SessionResponse session = mfaVerifyUseCase.verify(request);
        return ResponseEntity.ok(ApiResponse.ok("MFA verificado correctamente", session));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        logoutUseCase.logout(token);
        return ResponseEntity.ok(ApiResponse.ok("Sesión cerrada correctamente", null));
    }
}