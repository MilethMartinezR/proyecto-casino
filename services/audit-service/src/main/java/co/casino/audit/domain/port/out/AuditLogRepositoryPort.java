package co.casino.audit.domain.port.out;

import co.casino.audit.domain.model.AuditLog;

import java.util.List;

public interface AuditLogRepositoryPort {
    void save(AuditLog log);
  List<AuditLog> findByUserId(String userId);

List<AuditLog> findByGameId(String gameId);

List<AuditLog> findByEventType(String eventType);
}