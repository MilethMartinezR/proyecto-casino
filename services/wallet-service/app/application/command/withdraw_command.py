from dataclasses import dataclass


@dataclass
class WithdrawCommand:
    usuario_id: str
    monto_creditos: float
    cuenta_destino: str


@dataclass
class ExecuteWithdrawCommand:
    solicitud_id: str
