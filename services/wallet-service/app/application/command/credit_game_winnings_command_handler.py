from datetime import datetime
from app.domain.model.transaction import Transaction, TipoTransaccion, EstadoTx
from app.domain.port.out.wallet_repository_port import WalletRepositoryPort
from app.domain.port.out.event_publisher_port import EventPublisherPort
from app.domain.exception.wallet_not_found_error import WalletNotFoundError

TOPIC_WALLET_EVENTS = "wallet-events"


class CreditGameWinningsCommandHandler:
    """Acredita ganancias de partida directamente en créditos (invocado por Kafka consumer)."""

    def __init__(self, repository: WalletRepositoryPort, event_publisher: EventPublisherPort):
        self._repo = repository
        self._publisher = event_publisher

    async def execute(self, usuario_id: str, monto_creditos: float, game_id: str) -> None:
        wallet = self._repo.find_by_usuario_id(usuario_id)
        if not wallet:
            raise WalletNotFoundError(usuario_id)

        wallet.acreditar(monto_creditos)
        self._repo.save_wallet(wallet)

        tx = Transaction(
            wallet_id=wallet.wallet_id,
            tipo=TipoTransaccion.GANANCIA,
            monto=monto_creditos / wallet.tasa_cambio,
            monto_creditos=monto_creditos,
            estado=EstadoTx.COMPLETADA,
            descripcion=f"Ganancia partida {game_id}: {monto_creditos} créditos",
        )
        self._repo.save_transaction(tx)

        await self._publisher.publish({
            "event_type": "GANANCIA_ACREDITADA",
            "usuario_id": usuario_id,
            "wallet_id": wallet.wallet_id,
            "monto_creditos": monto_creditos,
            "game_id": game_id,
            "transaccion_id": tx.transaccion_id,
            "timestamp": datetime.utcnow().isoformat(),
        }, TOPIC_WALLET_EVENTS)
