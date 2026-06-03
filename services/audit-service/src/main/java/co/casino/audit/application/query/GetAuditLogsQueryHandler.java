package co.casino.audit.application.query;

import co.casino.audit.domain.model.AuditLog;
import co.casino.audit.domain.port.in.QueryAuditLogsUseCase;
import co.casino.audit.domain.port.out.AuditLogRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAuditLogsQueryHandler implements QueryAuditLogsUseCase {

    private final AuditLogRepositoryPort auditLogRepository;

    @Override
    public List<AuditLog> findByUserId(String userId) {
        return auditLogRepository.findByUserId(userId);
    }

    @Override
    public List<AuditLog> findByGameId(String gameId) {
        return auditLogRepository.findByGameId(gameId);
    }

    @Override
    public List<AuditLog> findByEventType(String eventType) {
        return auditLogRepository.findByEventType(eventType);
    }
}