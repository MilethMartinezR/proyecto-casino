import uuid
from datetime import datetime
from sqlalchemy import Column, String, Enum, DateTime, Double
from app.infrastructure.adapter.out.persistence.database import Base
from app.domain.model.report import TipoReporte


class ReporteModel(Base):
    __tablename__ = "reportes"

    reporte_id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    generado_por = Column(String(36), nullable=False)
    tipo = Column(Enum(TipoReporte), nullable=False)
    periodo_inicio = Column(DateTime, nullable=False)
    periodo_fin = Column(DateTime, nullable=False)
    total_creditos_comprados = Column(Double, default=0.0)
    total_creditos_apostados = Column(Double, default=0.0)
    total_creditos_ganados = Column(Double, default=0.0)
    balance_global = Column(Double, default=0.0)
    fecha_generacion = Column(DateTime, default=datetime.utcnow)
