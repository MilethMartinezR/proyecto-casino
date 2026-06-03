package co.casino.auth.application.usecase;

import co.casino.auth.application.dto.request.AuthRequest;
import co.casino.auth.application.dto.response.UserResponse;
import co.casino.auth.domain.exception.UserAlreadyExistsException;
import co.casino.auth.domain.model.User;
import co.casino.auth.domain.port.in.RegisterUserUseCase;
import co.casino.auth.domain.port.out.AuthEventPublisherPort;
import co.casino.auth.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final AuthEventPublisherPort eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCaseImpl(UserRepositoryPort userRepository,
                                   AuthEventPublisherPort eventPublisher,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse register(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Ya existe un usuario con el email: " + request.getEmail());
        }
        if (request.getUsername() != null && userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Ya existe un usuario con el username: " + request.getUsername());
        }

        User user = new User(
            UUID.randomUUID().toString(),
            request.getUsername() != null ? request.getUsername() : request.getEmail().split("@")[0],
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            false,
            null,
            "PLAYER",
            "ACTIVE",
            Instant.now(),
            Instant.now()
        );

        User saved = userRepository.save(user);
        eventPublisher.publishUserRegistered(saved);

        return new UserResponse(saved.getId(), saved.getUsername(), saved.getEmail(),
                                saved.getRole(), saved.isMfaEnabled());
    }
}