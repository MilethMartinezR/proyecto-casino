from abc import ABC, abstractmethod
from app.domain.model.transaction import Transaction


class DepositUseCase(ABC):
    @abstractmethod
    def execute(self, usuario_id: str, monto_usd: float) -> Transaction:
        ...
