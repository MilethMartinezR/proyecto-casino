from abc import ABC, abstractmethod
from datetime import datetime
from app.domain.model.report import Report, TipoReporte


class GenerateReportUseCase(ABC):
    @abstractmethod
    def execute(self, admin_id: str, tipo: TipoReporte, inicio: datetime, fin: datetime) -> Report:
        ...
