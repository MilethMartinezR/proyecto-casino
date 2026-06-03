package co.casino.audit.infrastructure.adapter.in.rest;

import co.casino.audit.application.dto.response.AuditLogResponse;
import co.casino.audit.application.dto.response.AuditReportResponse;
import co.casino.audit.application.query.GetReportsQueryHandler;
import co.casino.audit.domain.model.AuditLog;
import co.casino.audit.domain.model.AuditReport;
import co.casino.audit.domain.port.in.GenerateReportUseCase;
import co.casino.audit.domain.port.in.QueryAuditLogsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@Tag(
    name = "Audit Service",
    description = "API para consulta de logs y generación de reportes de auditoría"
)
public class AuditController {

    private final QueryAuditLogsUseCase queryAuditLogs;
    private final GenerateReportUseCase generateReport;
    private final GetReportsQueryHandler getReportsQuery;

    @Operation(
        summary = "Consultar logs de auditoría",
        description = "Permite consultar logs por userId, gameId o eventType"
    )
    @GetMapping("/logs")
    public ResponseEntity<List<AuditLogResponse>> getLogs(

            @Parameter(description = "Identificador del usuario")
            @RequestParam(required = false) String userId,

            @Parameter(description = "Identificador de la partida")
            @RequestParam(required = false) String gameId,

            @Parameter(description = "Tipo de evento")
            @RequestParam(required = false) String eventType) {

        List<AuditLog> results;

        if (userId != null) {
            results = queryAuditLogs.findByUserId(userId);
        } else if (gameId != null) {
            results = queryAuditLogs.findByGameId(gameId);
        } else if (eventType != null) {
            results = queryAuditLogs.findByEventType(eventType);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                results.stream()
                        .map(this::toLogResponse)
                        .collect(Collectors.toList())
        );
    }

    @Operation(
        summary = "Generar reporte de auditoría",
        description = "Genera un reporte consolidado para un usuario específico"
    )
    @PostMapping("/reports/generate")
    public ResponseEntity<AuditReportResponse> generateReport(
            @Parameter(description = "Identificador del usuario")
            @RequestParam String userId) {

        AuditReport report = generateReport.execute(userId);
        return ResponseEntity.ok(toReportResponse(report));
    }

    @Operation(
        summary = "Consultar reportes",
        description = "Obtiene todos los reportes o uno específico por usuario"
    )
    @GetMapping("/reports")
    public ResponseEntity<?> getReport(
            @Parameter(description = "Identificador del usuario")
            @RequestParam(required = false) String userId) {

        if (userId != null) {
            return getReportsQuery.findByUserId(userId)
                    .map(r -> ResponseEntity.ok(toReportResponse(r)))
                    .orElse(ResponseEntity.notFound().build());
        }

        List<AuditReportResponse> all = getReportsQuery.findAll()
                .stream()
                .map(this::toReportResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(all);
    }

    // --- mappers ---

    private AuditLogResponse toLogResponse(AuditLog log) {
        return AuditLogResponse.builder()
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

    private AuditReportResponse toReportResponse(AuditReport report) {
        return AuditReportResponse.builder()
                .id(report.getId())
                .userId(report.getUserId())
                .totalPartidas(report.getTotalPartidas())
                .totalGanadas(report.getTotalGanadas())
                .totalPerdidas(report.getTotalPerdidas())
                .totalAbandonadas(report.getTotalAbandonadas())
                .totalApostado(report.getTotalApostado())
                .totalPagado(report.getTotalPagado())
                .generatedAt(report.getGeneratedAt())
                .build();
    }
}