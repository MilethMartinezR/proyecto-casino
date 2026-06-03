package co.casino.auth.infrastructure.adapter.out.persistence.repository;

import co.casino.auth.domain.model.Session;
import co.casino.auth.domain.port.out.SessionRepositoryPort;
import co.casino.auth.infrastructure.adapter.out.persistence.document.SessionDocument;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SessionRepositoryAdapter implements SessionRepositoryPort {

    private final SessionMongoRepository mongo;

    public SessionRepositoryAdapter(SessionMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Session save(Session session) {
        return toModel(mongo.save(toDocument(session)));
    }

    @Override
    public Optional<Session> findByToken(String token) {
        return mongo.findByToken(token).map(this::toModel);
    }

    @Override
    public void deactivateByToken(String token) {
        mongo.findByToken(token).ifPresent(doc -> {
            doc.setActive(false);
            mongo.save(doc);
        });
    }

    private SessionDocument toDocument(Session s) {
        SessionDocument d = new SessionDocument();
        d.setId(s.getId());
        d.setUserId(s.getUserId());
        d.setToken(s.getToken());
        d.setIpAddress(s.getIpAddress());
        d.setUserAgent(s.getUserAgent());
        d.setActive(s.isActive());
        d.setCreatedAt(s.getCreatedAt());
        d.setExpiresAt(s.getExpiresAt());
        return d;
    }

    private Session toModel(SessionDocument d) {
        return new Session(d.getId(), d.getUserId(), d.getToken(), d.getIpAddress(),
                           d.getUserAgent(), d.isActive(), d.getCreatedAt(), d.getExpiresAt());
    }
}