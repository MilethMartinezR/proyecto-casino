from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum


class EstadoUsuario(str, Enum):
    ACTIVO = "ACTIVO"
    SUSPENDIDO = "SUSPENDIDO"
    PENDIENTE_VERIFICACION = "PENDIENTE_VERIFICACION"


@dataclass
class AdminUser:
    usuario_id: str
    nombre: str
    email: str
    estado: EstadoUsuario = EstadoUsuario.PENDIENTE_VERIFICACION
    mfa_habilitado: bool = False
    fecha_registro: datetime = field(default_factory=datetime.utcnow)
    fecha_actualizacion: datetime = field(default_factory=datetime.utcnow)

    def suspender(self) -> None:
        self.estado = EstadoUsuario.SUSPENDIDO
        self.fecha_actualizacion = datetime.utcnow()

    def activar(self) -> None:
        self.estado = EstadoUsuario.ACTIVO
        self.fecha_actualizacion = datetime.utcnow()
