package co.casino.audit.application.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuditLogCommand {
    private String gameId;
    private String userId;
    private String eventType;
    private String estado;
    private Double apuesta;
    private Double pago;
    private String timestamp;
}