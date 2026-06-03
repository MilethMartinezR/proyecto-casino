package co.casino.audit.domain.port.in;

import co.casino.audit.domain.model.AuditReport;

public interface GenerateReportUseCase {
    AuditReport execute(String userId);
}