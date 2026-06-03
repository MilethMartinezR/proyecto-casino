package co.casino.auth.infrastructure.adapter.out.persistence.repository;

import co.casino.auth.infrastructure.adapter.out.persistence.document.SessionMFADocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SessionMFAMongoRepository extends MongoRepository<SessionMFADocument, String> {
    Optional<SessionMFADocument> findByTempToken(String tempToken);
    void deleteByTempToken(String tempToken);
}