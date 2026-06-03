package co.casino.audit.domain.port.in;

import co.casino.audit.domain.model.AuditLog;

public interface CreateAuditLogUseCase {
    void execute(AuditLog log);
}