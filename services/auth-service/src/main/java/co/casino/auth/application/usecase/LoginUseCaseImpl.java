package co.casino.auth.application.usecase;

import co.casino.auth.application.dto.request.AuthRequest;
import co.casino.auth.application.dto.response.SessionResponse;
import co.casino.auth.domain.exception.InvalidCredentialsException;
import co.casino.auth.domain.model.Session;
import co.casino.auth.domain.model.SessionMFA;
import co.casino.auth.domain.model.User;
import co.casino.auth.domain.port.in.LoginUseCase;
import co.casino.auth.domain.port.out.AuthEventPublisherPort;
import co.casino.auth.domain.port.out.SessionMFARepositoryPort;
import co.casino.auth.domain.port.out.SessionRepositoryPort;
import co.casino.auth.domain.port.out.UserRepositoryPort;
import co.casino.auth.infrastructure.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final SessionRepositoryPort sessionRepository;
    private final SessionMFARepositoryPort sessionMFARepository;
    private final AuthEventPublisherPort eventPublisher;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.mfa-expiration-ms:300000}")
    private long mfaExpirationMs;

    public LoginUseCaseImpl(UserRepositoryPort userRepository,
                            SessionRepositoryPort sessionRepository,
                            SessionMFARepositoryPort sessionMFARepository,
                            AuthEventPublisherPort eventPublisher,
                            JwtService jwtService,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.sessionMFARepository = sessionMFARepository;
        this.eventPublisher = eventPublisher;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SessionResponse login(AuthRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new InvalidCredentialsException();
        }

        // Si MFA está habilitado, emitir un temp token
        if (user.isMfaEnabled()) {
            String tempToken = UUID.randomUUID().toString();
            SessionMFA mfaSession = new SessionMFA(
                UUID.randomUUID().toString(),
                user.getId(),
                tempToken,
                false,
                Instant.now(),
                Instant.now().plusMillis(mfaExpirationMs)
            );
            sessionMFARepository.save(mfaSession);
            return new SessionResponse(null, user.getId(), true, tempToken);
        }

        // Sin MFA: crear sesión directamente
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
        Session session = new Session(
            UUID.randomUUID().toString(),
            user.getId(),
            token,
            ipAddress,
            userAgent,
            true,
            Instant.now(),
            Instant.now().plusMillis(jwtService.getExpirationMs())
        );
        sessionRepository.save(session);
        eventPublisher.publishUserLoggedIn(user);

        return new SessionResponse(token, user.getId(), false, null);
    }
}