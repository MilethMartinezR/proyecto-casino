from datetime import datetime
from app.application.command.deposit_command import DepositCommand
from app.domain.model.transaction import Transaction, TipoTransaccion, EstadoTx
from app.domain.model.wallet import Wallet
from app.domain.port.inbound.deposit_use_case import DepositUseCase
from app.domain.port.out.wallet_repository_port import WalletRepositoryPort
from app.domain.port.out.event_publisher_port import EventPublisherPort

TOPIC_WALLET_EVENTS = "wallet-events"


class DepositCommandHandler(DepositUseCase):
    def __init__(
        self,
        repository: WalletRepositoryPort,
        event_publisher: EventPublisherPort,
    ):
        self._repo = repository
        self._publisher = event_publisher

    def execute(self, usuario_id: str, monto_usd: float) -> Transaction:
        wallet = self._repo.find_by_usuario_id(usuario_id)
        if not wallet:
            wallet = Wallet(usuario_id=usuario_id)
            wallet = self._repo.save_wallet(wallet)

        monto_creditos = monto_usd * wallet.tasa_cambio
        wallet.acreditar(monto_creditos)
        self._repo.save_wallet(wallet)

        tx = Transaction(
            wallet_id=wallet.wallet_id,
            tipo=TipoTransaccion.DEPOSITO,
            monto=monto_usd,
            monto_creditos=monto_creditos,
            estado=EstadoTx.COMPLETADA,
            descripcion=f"Compra de créditos: ${monto_usd} USD",
        )
        tx = self._repo.save_transaction(tx)
        return tx
