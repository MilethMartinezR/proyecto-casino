from abc import ABC, abstractmethod
from app.domain.model.wallet import Wallet


class GetBalanceUseCase(ABC):
    @abstractmethod
    def execute(self, usuario_id: str) -> Wallet:
        ...
