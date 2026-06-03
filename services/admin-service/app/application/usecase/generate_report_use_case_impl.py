from datetime import datetime
from app.domain.model.report import Report, TipoReporte
from app.domain.port.inbound.generate_report_use_case import GenerateReportUseCase
from app.domain.port.out.admin_repository_port import AdminRepositoryPort


class GenerateReportUseCaseImpl(GenerateReportUseCase):
    def __init__(self, repository: AdminRepositoryPort):
        self._repo = repository

    def execute(self, admin_id: str, tipo: TipoReporte, inicio: datetime, fin: datetime) -> Report:
        datos = self._repo.query_transacciones(inicio, fin)
        report = Report(
            generado_por=admin_id,
            tipo=tipo,
            periodo_inicio=inicio,
            periodo_fin=fin,
            **datos,
        )
        return self._repo.save_report(report)
