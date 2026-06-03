package co.casino.audit.infrastructure.adapter.out.persistence.repository;

import co.casino.audit.domain.model.AuditReport;
import co.casino.audit.domain.port.out.AuditReportRepositoryPort;
import co.casino.audit.infrastructure.adapter.out.persistence.document.AuditReportDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuditReportRepositoryAdapter implements AuditReportRepositoryPort {

    private final AuditReportMongoRepository mongoRepository;

    @Override
    public void save(AuditReport report) {
        // Upsert por userId: si ya existe, lo reemplaza
        mongoRepository.findByUserId(report.getUserId()).ifPresentOrElse(
                existing -> {
                    AuditReportDocument updated = toDocument(report);
                    updated.setId(existing.getId());
                    mongoRepository.save(updated);
                },
                () -> mongoRepository.save(toDocument(report))
        );
    }

    @Override
    public Optional<AuditReport> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public List<AuditReport> findAll() {
        return mongoRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private AuditReportDocument toDocument(AuditReport report) {
        return AuditReportDocument.builder()
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

    private AuditReport toDomain(AuditReportDocument doc) {
        return AuditReport.builder()
                .id(doc.getId())
                .userId(doc.getUserId())
                .totalPartidas(doc.getTotalPartidas())
                .totalGanadas(doc.getTotalGanadas())
                .totalPerdidas(doc.getTotalPerdidas())
                .totalAbandonadas(doc.getTotalAbandonadas())
                .totalApostado(doc.getTotalApostado())
                .totalPagado(doc.getTotalPagado())
                .generatedAt(doc.getGeneratedAt())
                .build();
    }
}