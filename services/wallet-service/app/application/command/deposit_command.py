from dataclasses import dataclass


@dataclass
class DepositCommand:
    usuario_id: str
    monto_usd: float
