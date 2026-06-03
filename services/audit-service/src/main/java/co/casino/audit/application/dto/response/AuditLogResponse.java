package co.casino.audit.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private String id;
    private String gameId;
    private String userId;
    private String eventType;
    private String estado;
    private Double apuesta;
    private Double pago;
    private String timestamp;
    private String receivedAt;
}