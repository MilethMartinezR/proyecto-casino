from pydantic import BaseModel
from datetime import datetime
from typing import Optional
from app.domain.model.transaction import TipoTransaccion, EstadoTx


class TransaccionResponse(BaseModel):
    transaccion_id: str
    wallet_id: str
    tipo: TipoTransaccion
    monto: float
    monto_creditos: float
    estado: EstadoTx
    fecha: datetime
    descripcion: Optional[str]

    model_config = {"from_attributes": True}


class SolicitudRetiroResponse(BaseModel):
    solicitud_id: str
    transaccion_id: str
    cuenta_destino: str
    estado: EstadoTx
    fecha_solicitud: datetime
    fecha_ejecucion: Optional[datetime]

    model_config = {"from_attributes": True}


class KafkaWalletEvent(BaseModel):
    event_type: str
    usuario_id: str
    wallet_id: str
    monto_creditos: float
    transaccion_id: str
    timestamp: str
