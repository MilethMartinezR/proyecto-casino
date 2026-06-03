package co.casino.audit.application.query;

import co.casino.audit.domain.model.AuditReport;
import co.casino.audit.domain.port.out.AuditReportRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetReportsQueryHandler {

    private final AuditReportRepositoryPort auditReportRepository;

    public Optional<AuditReport> findByUserId(String userId) {
        return auditReportRepository.findByUserId(userId);
    }

    public List<AuditReport> findAll() {
        return auditReportRepository.findAll();
    }
}