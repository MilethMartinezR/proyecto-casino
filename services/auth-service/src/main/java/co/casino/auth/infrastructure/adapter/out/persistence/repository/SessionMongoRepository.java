package co.casino.auth.infrastructure.adapter.out.persistence.repository;

import co.casino.auth.infrastructure.adapter.out.persistence.document.SessionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SessionMongoRepository extends MongoRepository<SessionDocument, String> {
    Optional<SessionDocument> findByToken(String token);
}