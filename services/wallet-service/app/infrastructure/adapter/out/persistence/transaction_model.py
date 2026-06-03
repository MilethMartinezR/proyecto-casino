import uuid
from datetime import datetime
from sqlalchemy import Column, String, Double, Enum, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from app.infrastructure.adapter.out.persistence.database import Base
from app.domain.model.transaction import TipoTransaccion, EstadoTx


class TransactionModel(Base):
    __tablename__ = "transacciones"

    transaccion_id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    wallet_id = Column(String(36), ForeignKey("wallets.wallet_id"), nullable=False, index=True)
    tipo = Column(Enum(TipoTransaccion), nullable=False)
    monto = Column(Double, nullable=False)
    monto_creditos = Column(Double, nullable=False)
    estado = Column(Enum(EstadoTx), default=EstadoTx.PENDIENTE)
    fecha = Column(DateTime, default=datetime.utcnow)
    descripcion = Column(String(255))

    wallet = relationship("WalletModel", back_populates="transacciones")
    solicitud_retiro = relationship("SolicitudRetiroModel", back_populates="transaccion", uselist=False)


class SolicitudRetiroModel(Base):
    __tablename__ = "solicitudes_retiro"

    solicitud_id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    transaccion_id = Column(String(36), ForeignKey("transacciones.transaccion_id"), nullable=False)
    cuenta_destino = Column(String(100), nullable=False)
    estado = Column(Enum(EstadoTx), default=EstadoTx.PENDIENTE)
    fecha_solicitud = Column(DateTime, default=datetime.utcnow)
    fecha_ejecucion = Column(DateTime, nullable=True)

    transaccion = relationship("TransactionModel", back_populates="solicitud_retiro")
