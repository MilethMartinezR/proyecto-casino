package co.casino.audit.domain.port.in;

import co.casino.audit.domain.model.AuditLog;

import java.util.List;

public interface QueryAuditLogsUseCase {
    List<AuditLog> findByUserId(String userId);
    List<AuditLog> findByGameId(String gameId);
    List<AuditLog> findByEventType(String eventType);
}