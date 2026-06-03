package co.casino.audit.infrastructure.adapter.out.persistence.repository;

import co.casino.audit.infrastructure.adapter.out.persistence.document.AuditReportDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuditReportMongoRepository extends MongoRepository<AuditReportDocument, String> {
    Optional<AuditReportDocument> findByUserId(String userId);
}