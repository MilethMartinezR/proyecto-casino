import uuid
from datetime import datetime
from sqlalchemy import Column, String, Enum, DateTime, Boolean, Double, Index
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


class MovimientoJuegoModel(Base):
    __tablename__ = "movimientos_juego"

    id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    usuario_id = Column(String(36), nullable=False, index=True)
    game_id = Column(String(36), nullable=False)
    event_type = Column(String(50), nullable=False)
    monto_apostado = Column(Double, default=0.0)
    monto_ganado = Column(Double, default=0.0)
    timestamp = Column(DateTime, default=datetime.utcnow, index=True)
