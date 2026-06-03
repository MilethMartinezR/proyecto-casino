from abc import ABC, abstractmethod
from typing import Optional, List
from app.domain.model.wallet import Wallet
from app.domain.model.transaction import Transaction, SolicitudRetiro


class WalletRepositoryPort(ABC):
    @abstractmethod
    def find_by_usuario_id(self, usuario_id: str) -> Optional[Wallet]:
        ...

    @abstractmethod
    def save_wallet(self, wallet: Wallet) -> Wallet:
        ...

    @abstractmethod
    def save_transaction(self, transaction: Transaction) -> Transaction:
        ...

    @abstractmethod
    def save_solicitud_retiro(self, solicitud: SolicitudRetiro) -> SolicitudRetiro:
        ...

    @abstractmethod
    def find_solicitud_by_id(self, solicitud_id: str) -> Optional[SolicitudRetiro]:
        ...

    @abstractmethod
    def find_transactions_by_wallet(self, wallet_id: str) -> List[Transaction]:
        ...

    @abstractmethod
    def find_transaction_by_id(self, transaccion_id: str) -> Optional[Transaction]:
        ...
