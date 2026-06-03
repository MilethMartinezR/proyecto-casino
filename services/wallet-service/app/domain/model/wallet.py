from dataclasses import dataclass, field
from datetime import datetime
import uuid


@dataclass
class Wallet:
    usuario_id: str
    saldo_creditos: float = 0.0
    tasa_cambio: float = 1000.0  # 1 USD = 1000 créditos
    wallet_id: str = field(default_factory=lambda: str(uuid.uuid4()))
    fecha_actualizacion: datetime = field(default_factory=datetime.utcnow)

    def tiene_fondos(self, monto_creditos: float) -> bool:
        return self.saldo_creditos >= monto_creditos

    def acreditar(self, monto_creditos: float) -> None:
        self.saldo_creditos += monto_creditos
        self.fecha_actualizacion = datetime.utcnow()

    def debitar(self, monto_creditos: float) -> None:
        self.saldo_creditos -= monto_creditos
        self.fecha_actualizacion = datetime.utcnow()
