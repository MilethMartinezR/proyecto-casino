from pydantic import BaseModel, Field
from datetime import datetime


class WalletResponse(BaseModel):
    wallet_id: str
    usuario_id: str
    saldo_creditos: float
    tasa_cambio: float
    fecha_actualizacion: datetime

    model_config = {"from_attributes": True}


class DepositRequest(BaseModel):
    usuario_id: str
    monto_usd: float = Field(..., gt=0, description="Monto en USD a depositar")


class WithdrawRequest(BaseModel):
    usuario_id: str
    monto_creditos: float = Field(..., gt=0, description="Créditos a retirar")
    cuenta_destino: str = Field(..., min_length=3)


class CobrarApuestaRequest(BaseModel):
    usuario_id: str
    monto_creditos: float = Field(..., gt=0, description="Créditos a descontar por apuesta")
    descripcion: str = None
