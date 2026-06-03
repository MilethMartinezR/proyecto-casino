package co.casino.audit.infrastructure.adapter.out.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_reports")
public class AuditReportDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private long totalPartidas;
    private long totalGanadas;
    private long totalPerdidas;
    private long totalAbandonadas;
    private Double totalApostado;
    private Double totalPagado;
    private String generatedAt;
}