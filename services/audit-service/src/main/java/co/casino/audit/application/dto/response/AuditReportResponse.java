package co.casino.audit.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditReportResponse {
    private String id;
    private String userId;
    private long totalPartidas;
    private long totalGanadas;
    private long totalPerdidas;
    private long totalAbandonadas;
    private Double totalApostado;
    private Double totalPagado;
    private String generatedAt;
}