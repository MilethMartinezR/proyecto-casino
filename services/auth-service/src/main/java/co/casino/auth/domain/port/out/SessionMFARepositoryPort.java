package co.casino.auth.domain.port.out;

import co.casino.auth.domain.model.SessionMFA;
import java.util.Optional;

public interface SessionMFARepositoryPort {
    SessionMFA save(SessionMFA sessionMFA);
    Optional<SessionMFA> findByTempToken(String tempToken);
    void deleteByTempToken(String tempToken);
}