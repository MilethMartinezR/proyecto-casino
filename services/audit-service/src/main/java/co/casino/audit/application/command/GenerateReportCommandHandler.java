package co.casino.audit.application.command;

import co.casino.audit.domain.model.AuditLog;
import co.casino.audit.domain.model.AuditReport;
import co.casino.audit.domain.port.in.GenerateReportUseCase;
import co.casino.audit.domain.port.out.AuditLogRepositoryPort;
import co.casino.audit.domain.port.out.AuditReportRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerateReportCommandHandler implements GenerateReportUseCase {

    private final AuditLogRepositoryPort auditLogRepository;
    private final AuditReportRepositoryPort auditReportRepository;

    @Override
    public AuditReport execute(String userId) {
        List<AuditLog> logs = auditLogRepository.findByUserId(userId);

        long ganadas = logs.stream()
                .filter(l -> "JUGADOR_GANO".equalsIgnoreCase(l.getEstado()))
                .count();
        long perdidas = logs.stream()
                .filter(l -> "JUGADOR_PERDIO".equalsIgnoreCase(l.getEstado()))
                .count();
        long abandonadas = logs.stream()
                .filter(l -> "GAME_ABANDONED".equalsIgnoreCase(l.getEventType()))
                .count();
        double totalApostado = logs.stream()
                .filter(l -> l.getApuesta() != null)
                .mapToDouble(AuditLog::getApuesta)
                .sum();
        double totalPagado = logs.stream()
                .filter(l -> l.getPago() != null)
                .mapToDouble(AuditLog::getPago)
                .sum();

        AuditReport report = AuditReport.builder()
                .userId(userId)
                .totalPartidas((long) logs.size())
                .totalGanadas(ganadas)
                .totalPerdidas(perdidas)
                .totalAbandonadas(abandonadas)
                .totalApostado(totalApostado)
                .totalPagado(totalPagado)
                .generatedAt(Instant.now().toString())
                .build();

        auditReportRepository.save(report);
        return report;
    }
}