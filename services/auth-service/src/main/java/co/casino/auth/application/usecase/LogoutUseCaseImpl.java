package co.casino.auth.application.usecase;

import co.casino.auth.domain.port.in.LogoutUseCase;
import co.casino.auth.domain.port.out.SessionRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final SessionRepositoryPort sessionRepository;

    public LogoutUseCaseImpl(SessionRepositoryPort sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void logout(String token) {
        sessionRepository.deactivateByToken(token);
    }
}