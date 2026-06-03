from app.domain.model.wallet import Wallet
from app.domain.port.inbound.get_balance_use_case import GetBalanceUseCase
from app.domain.port.out.wallet_repository_port import WalletRepositoryPort
from app.domain.exception.wallet_not_found_error import WalletNotFoundError


class GetBalanceQueryHandler(GetBalanceUseCase):
    def __init__(self, repository: WalletRepositoryPort):
        self._repo = repository

    def execute(self, usuario_id: str) -> Wallet:
        wallet = self._repo.find_by_usuario_id(usuario_id)
        if not wallet:
            raise WalletNotFoundError(usuario_id)
        return wallet
