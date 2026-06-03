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
import co.casino.auth.domain.port.out.SessionMFARepositoryPort;
import co.casino.auth.domain.port.out.UserRepositoryPort;
import co.casino.auth.infrastructure.config.TotpConfig;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
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
    private final SessionMFARepositoryPort sessionMFARepository;
    private final UserRepositoryPort userRepository;
    private final CodeGenerator codeGenerator;
    private final TimeProvider timeProvider;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase,
                          MfaVerifyUseCase mfaVerifyUseCase,
                          LogoutUseCase logoutUseCase,
                          SessionMFARepositoryPort sessionMFARepository,
                          UserRepositoryPort userRepository,
                          CodeGenerator codeGenerator,
                          TimeProvider timeProvider) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.mfaVerifyUseCase = mfaVerifyUseCase;
        this.logoutUseCase = logoutUseCase;
        this.sessionMFARepository = sessionMFARepository;
        this.userRepository = userRepository;
        this.codeGenerator = codeGenerator;
        this.timeProvider = timeProvider;
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

        // El frontend lee el JWT del header Authorization de la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + session.getToken());

        return ResponseEntity.ok()
            .headers(headers)
            .body(ApiResponse.ok("MFA verificado correctamente", session));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        logoutUseCase.logout(token);
        return ResponseEntity.ok(ApiResponse.ok("Sesión cerrada correctamente", null));
    }

    /**
     * Endpoint de desarrollo: devuelve el código TOTP actual para el tempToken dado.
     * Permite al frontend hacer MFA sin necesidad de app autenticadora.
     */
    @GetMapping("/mfa/code/{tempToken}")
    public ResponseEntity<ApiResponse<Object>> getDevMfaCode(@PathVariable String tempToken) {
        var mfaSession = sessionMFARepository.findByTempToken(tempToken);
        if (mfaSession.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.ok("Sesión MFA no encontrada", null));
        }
        var user = userRepository.findById(mfaSession.get().getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.ok("Usuario no encontrado", null));
        }
        try {
            String code = codeGenerator.generate(user.get().getMfaSecret(),
                Math.floorDiv(timeProvider.getTime(), 30));
            java.util.Map<String, String> body = new java.util.HashMap<>();
            body.put("mfaCode", code);
            return ResponseEntity.ok(ApiResponse.ok("Código MFA", body));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.ok("Error generando código", null));
        }
    }
}
