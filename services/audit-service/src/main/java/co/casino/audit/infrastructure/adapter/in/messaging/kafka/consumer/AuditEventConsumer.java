package co.casino.audit.infrastructure.adapter.in.messaging.kafka.consumer;

import co.casino.audit.domain.model.AuditLog;
import co.casino.audit.domain.port.in.CreateAuditLogUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final CreateAuditLogUseCase createAuditLog;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "game-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);

            AuditLog auditLog = AuditLog.builder()
                    .gameId(getTextOrNull(node, "gameId"))
                    .userId(getTextOrNull(node, "userId"))
                    .eventType(getTextOrNull(node, "eventType"))
                    .estado(getTextOrNull(node, "estado"))
                    .apuesta(getDoubleOrNull(node, "apuesta"))
                    .pago(getDoubleOrNull(node, "pago"))
                    .timestamp(getTextOrNull(node, "timestamp"))
                    .receivedAt(Instant.now().toString())
                    .build();

            createAuditLog.execute(auditLog);

            log.info("Evento auditado: type={} gameId={} userId={}",
                    auditLog.getEventType(), auditLog.getGameId(), auditLog.getUserId());

        } catch (Exception e) {
            // Nunca relanzar — si falla, Kafka deja de entregar mensajes
            log.error("Error procesando evento audit: {} | mensaje: {}", e.getMessage(), message);
        }
    }

    private String getTextOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value != null && !value.isNull()) ? value.asText() : null;
    }

    private Double getDoubleOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value != null && !value.isNull()) ? value.asDouble() : null;
    }
}