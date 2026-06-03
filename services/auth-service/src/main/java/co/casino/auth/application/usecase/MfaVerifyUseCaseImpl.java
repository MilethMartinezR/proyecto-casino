package co.casino.auth.application.usecase;

import co.casino.auth.application.dto.request.MFAVerifyRequest;
import co.casino.auth.application.dto.response.SessionResponse;
import co.casino.auth.domain.exception.InvalidMfaCodeException;
import co.casino.auth.domain.model.Session;
import co.casino.auth.domain.model.SessionMFA;
import co.casino.auth.domain.model.User;
import co.casino.auth.domain.port.in.MfaVerifyUseCase;
import co.casino.auth.domain.port.out.AuthEventPublisherPort;
import co.casino.auth.domain.port.out.SessionMFARepositoryPort;
import co.casino.auth.domain.port.out.SessionRepositoryPort;
import co.casino.auth.domain.port.out.UserRepositoryPort;
import co.casino.auth.infrastructure.security.JwtService;
import dev.samstevens.totp.code.CodeVerifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class MfaVerifyUseCaseImpl implements MfaVerifyUseCase {

    private final SessionMFARepositoryPort sessionMFARepository;
    private final SessionRepositoryPort sessionRepository;
    private final UserRepositoryPort userRepository;
    private final AuthEventPublisherPort eventPublisher;
    private final JwtService jwtService;
    private final CodeVerifier codeVerifier;

    public MfaVerifyUseCaseImpl(SessionMFARepositoryPort sessionMFARepository,
                                SessionRepositoryPort sessionRepository,
                                UserRepositoryPort userRepository,
                                AuthEventPublisherPort eventPublisher,
                                JwtService jwtService,
                                CodeVerifier codeVerifier) {
        this.sessionMFARepository = sessionMFARepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.jwtService = jwtService;
        this.codeVerifier = codeVerifier;
    }

    @Override
    public SessionResponse verify(MFAVerifyRequest request) {
        SessionMFA mfaSession = sessionMFARepository.findByTempToken(request.getTempToken())
            .orElseThrow(InvalidMfaCodeException::new);

        if (mfaSession.getExpiresAt().isBefore(Instant.now())) {
            sessionMFARepository.deleteByTempToken(request.getTempToken());
            throw new InvalidMfaCodeException();
        }

        User user = userRepository.findById(mfaSession.getUserId())
            .orElseThrow(InvalidMfaCodeException::new);

        if (!codeVerifier.isValidCode(user.getMfaSecret(), request.getCode())) {
            throw new InvalidMfaCodeException();
        }

        sessionMFARepository.deleteByTempToken(request.getTempToken());

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
        Session session = new Session(
            UUID.randomUUID().toString(),
            user.getId(),
            token,
            null,
            null,
            true,
            Instant.now(),
            Instant.now().plusMillis(jwtService.getExpirationMs())
        );
        sessionRepository.save(session);
        eventPublisher.publishUserLoggedIn(user);

        return new SessionResponse(token, user.getId(), false, null);
    }
}