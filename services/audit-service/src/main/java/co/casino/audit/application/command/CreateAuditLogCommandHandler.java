package co.casino.audit.application.command;

import co.casino.audit.domain.model.AuditLog;
import co.casino.audit.domain.port.in.CreateAuditLogUseCase;
import co.casino.audit.domain.port.out.AuditLogRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateAuditLogCommandHandler implements CreateAuditLogUseCase {

    private final AuditLogRepositoryPort repository;

    @Override
    public void execute(AuditLog log) {
        if (log.getReceivedAt() == null) {
            log.setReceivedAt(Instant.now().toString());
        }
        repository.save(log);
    }
}