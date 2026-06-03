from datetime import datetime
from sqlalchemy import Column, String, Enum, DateTime, Boolean
from app.infrastructure.adapter.out.persistence.database import Base
from app.domain.model.admin_user import EstadoUsuario


class UsuarioSnapshotModel(Base):
    __tablename__ = "usuarios_snapshot"

    usuario_id = Column(String(36), primary_key=True)
    nombre = Column(String(120), nullable=False)
    email = Column(String(255), nullable=False, unique=True, index=True)
    estado = Column(Enum(EstadoUsuario), default=EstadoUsuario.PENDIENTE_VERIFICACION)
    mfa_habilitado = Column(Boolean, default=False)
    fecha_registro = Column(DateTime, default=datetime.utcnow)
    fecha_actualizacion = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
