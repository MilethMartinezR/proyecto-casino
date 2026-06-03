import uuid
from datetime import datetime
from sqlalchemy import Column, String, Double, DateTime
from sqlalchemy.orm import relationship
from app.infrastructure.adapter.out.persistence.database import Base


class WalletModel(Base):
    __tablename__ = "wallets"

    wallet_id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    usuario_id = Column(String(36), nullable=False, unique=True, index=True)
    saldo_creditos = Column(Double, default=0.0, nullable=False)
    tasa_cambio = Column(Double, default=1000.0, nullable=False)
    fecha_actualizacion = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    transacciones = relationship("TransactionModel", back_populates="wallet", cascade="all, delete-orphan")
