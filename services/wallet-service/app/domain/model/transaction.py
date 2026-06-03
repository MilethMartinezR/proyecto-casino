from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum
from typing import Optional
import uuid


class TipoTransaccion(str, Enum):
    DEPOSITO = "DEPOSITO"
    RETIRO = "RETIRO"
    APUESTA = "APUESTA"
    GANANCIA = "GANANCIA"


class EstadoTx(str, Enum):
    PENDIENTE = "PENDIENTE"
    COMPLETADA = "COMPLETADA"
    FALLIDA = "FALLIDA"


@dataclass
class Transaction:
    wallet_id: str
    tipo: TipoTransaccion
    monto: float          # en USD
    monto_creditos: float
    estado: EstadoTx = EstadoTx.PENDIENTE
    descripcion: Optional[str] = None
    transaccion_id: str = field(default_factory=lambda: str(uuid.uuid4()))
    fecha: datetime = field(default_factory=datetime.utcnow)


@dataclass
class SolicitudRetiro:
    transaccion_id: str
    cuenta_destino: str
    estado: EstadoTx = EstadoTx.PENDIENTE
    solicitud_id: str = field(default_factory=lambda: str(uuid.uuid4()))
    fecha_solicitud: datetime = field(default_factory=datetime.utcnow)
    fecha_ejecucion: Optional[datetime] = None
