package co.casino.audit.infrastructure.adapter.out.persistence.repository;

import co.casino.audit.infrastructure.adapter.out.persistence.document.AuditLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogMongoRepository extends MongoRepository<AuditLogDocument, String> {
    List<AuditLogDocument> findByUserId(String userId);
    List<AuditLogDocument> findByGameId(String gameId);
    List<AuditLogDocument> findByEventType(String eventType);
}