from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum
import uuid


class TipoReporte(str, Enum):
    DIARIO = "DIARIO"
    SEMANAL = "SEMANAL"
    MENSUAL = "MENSUAL"


@dataclass
class Report:
    generado_por: str
    tipo: TipoReporte
    periodo_inicio: datetime
    periodo_fin: datetime
    total_creditos_comprados: float = 0.0
    total_creditos_apostados: float = 0.0
    total_creditos_ganados: float = 0.0
    balance_global: float = 0.0
    reporte_id: str = field(default_factory=lambda: str(uuid.uuid4()))
    fecha_generacion: datetime = field(default_factory=datetime.utcnow)
