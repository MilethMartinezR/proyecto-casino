package co.casino.audit.infrastructure.adapter.out.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.annotation.Collation;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
public class AuditLogDocument {

    @Id
    private String id;

    @Indexed
    private String gameId;

    @Indexed
    private String userId;

    @Indexed
    private String eventType;

    private String estado;
    private Double apuesta;
    private Double pago;
    private String timestamp;
    private String receivedAt;
}