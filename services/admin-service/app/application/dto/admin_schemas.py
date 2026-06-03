from pydantic import BaseModel
from datetime import datetime
from app.domain.model.admin_user import EstadoUsuario


class UsuarioSnapshotResponse(BaseModel):
    usuario_id: str
    nombre: str
    email: str
    estado: EstadoUsuario
    mfa_habilitado: bool
    fecha_registro: datetime
    fecha_actualizacion: datetime

    model_config = {"from_attributes": True}


class CambioEstadoRequest(BaseModel):
    admin_id: str
