package co.casino.audit.domain.port.out;

import co.casino.audit.domain.model.AuditReport;

import java.util.List;
import java.util.Optional;

public interface AuditReportRepositoryPort {
    void save(AuditReport report);
    Optional<AuditReport> findByUserId(String userId);
    List<AuditReport> findAll();
}