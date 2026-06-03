package co.casino.auth.domain.port.out;

import co.casino.auth.domain.model.Session;
import java.util.Optional;

public interface SessionRepositoryPort {
    Session save(Session session);
    Optional<Session> findByToken(String token);
    void deactivateByToken(String token);
}