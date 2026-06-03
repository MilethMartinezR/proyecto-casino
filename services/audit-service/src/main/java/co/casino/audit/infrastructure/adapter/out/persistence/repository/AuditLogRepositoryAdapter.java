package co.casino.audit.infrastructure.adapter.out.persistence.repository;

import co.casino.audit.domain.model.AuditLog;
import co.casino.audit.domain.port.out.AuditLogRepositoryPort;
import co.casino.audit.infrastructure.adapter.out.persistence.document.AuditLogDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepositoryPort {

    private final AuditLogMongoRepository mongoRepository;

    @Override
    public void save(AuditLog log) {
        mongoRepository.save(toDocument(log));
    }

    @Override
    public List<AuditLog> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findByGameId(String gameId) {
        return mongoRepository.findByGameId(gameId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findByEventType(String eventType) {
        return mongoRepository.findByEventType(eventType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // --- mappers ---

    private AuditLogDocument toDocument(AuditLog log) {
        return AuditLogDocument.builder()
                .id(log.getId())
                .gameId(log.getGameId())
                .userId(log.getUserId())
                .eventType(log.getEventType())
                .estado(log.getEstado())
                .apuesta(log.getApuesta())
                .pago(log.getPago())
                .timestamp(log.getTimestamp())
                .receivedAt(log.getReceivedAt())
                .build();
    }

    private AuditLog toDomain(AuditLogDocument doc) {
        return AuditLog.builder()
                .id(doc.getId())
                .gameId(doc.getGameId())
                .userId(doc.getUserId())
                .eventType(doc.getEventType())
                .estado(doc.getEstado())
                .apuesta(doc.getApuesta())
                .pago(doc.getPago())
                .timestamp(doc.getTimestamp())
                .receivedAt(doc.getReceivedAt())
                .build();
    }
}