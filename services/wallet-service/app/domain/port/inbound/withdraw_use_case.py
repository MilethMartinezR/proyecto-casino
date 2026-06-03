from abc import ABC, abstractmethod
from app.domain.model.transaction import SolicitudRetiro


class WithdrawUseCase(ABC):
    @abstractmethod
    def execute(self, usuario_id: str, monto_creditos: float, cuenta_destino: str) -> SolicitudRetiro:
        ...

    @abstractmethod
    def ejecutar_retiro(self, solicitud_id: str) -> SolicitudRetiro:
        ...
