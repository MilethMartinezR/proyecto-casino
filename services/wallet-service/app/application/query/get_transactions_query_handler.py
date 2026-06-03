from typing import List
from app.domain.model.transaction import Transaction
from app.domain.port.out.wallet_repository_port import WalletRepositoryPort
from app.domain.exception.wallet_not_found_error import WalletNotFoundError


class GetTransactionsQueryHandler:
    def __init__(self, repository: WalletRepositoryPort):
        self._repo = repository

    def execute(self, usuario_id: str) -> List[Transaction]:
        wallet = self._repo.find_by_usuario_id(usuario_id)
        if not wallet:
            raise WalletNotFoundError(usuario_id)
        return self._repo.find_transactions_by_wallet(wallet.wallet_id)
