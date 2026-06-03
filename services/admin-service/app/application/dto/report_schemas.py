from pydantic import BaseModel
from datetime import datetime
from app.domain.model.report import TipoReporte


class ReporteRequest(BaseModel):
    admin_id: str
    tipo: TipoReporte
    periodo_inicio: datetime
    periodo_fin: datetime


class ReporteResponse(BaseModel):
    reporte_id: str
    generado_por: str
    tipo: TipoReporte
    periodo_inicio: datetime
    periodo_fin: datetime
    total_creditos_comprados: float
    total_creditos_apostados: float
    total_creditos_ganados: float
    balance_global: float
    fecha_generacion: datetime

    model_config = {"from_attributes": True}
