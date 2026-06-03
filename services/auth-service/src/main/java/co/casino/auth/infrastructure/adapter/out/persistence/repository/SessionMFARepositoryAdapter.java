package co.casino.auth.infrastructure.adapter.out.persistence.repository;

import co.casino.auth.domain.model.SessionMFA;
import co.casino.auth.domain.port.out.SessionMFARepositoryPort;
import co.casino.auth.infrastructure.adapter.out.persistence.document.SessionMFADocument;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SessionMFARepositoryAdapter implements SessionMFARepositoryPort {

    private final SessionMFAMongoRepository mongo;

    public SessionMFARepositoryAdapter(SessionMFAMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public SessionMFA save(SessionMFA s) {
        return toModel(mongo.save(toDocument(s)));
    }

    @Override
    public Optional<SessionMFA> findByTempToken(String tempToken) {
        return mongo.findByTempToken(tempToken).map(this::toModel);
    }

    @Override
    public void deleteByTempToken(String tempToken) {
        mongo.deleteByTempToken(tempToken);
    }

    private SessionMFADocument toDocument(SessionMFA s) {
        SessionMFADocument d = new SessionMFADocument();
        d.setId(s.getId());
        d.setUserId(s.getUserId());
        d.setTempToken(s.getTempToken());
        d.setVerified(s.isVerified());
        d.setCreatedAt(s.getCreatedAt());
        d.setExpiresAt(s.getExpiresAt());
        return d;
    }

    private SessionMFA toModel(SessionMFADocument d) {
        return new SessionMFA(d.getId(), d.getUserId(), d.getTempToken(),
                              d.isVerified(), d.getCreatedAt(), d.getExpiresAt());
    }
}