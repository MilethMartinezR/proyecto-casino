from datetime import datetime
from app.domain.model.transaction import Transaction, SolicitudRetiro, TipoTransaccion, EstadoTx
from app.domain.port.inbound.withdraw_use_case import WithdrawUseCase
from app.domain.port.out.wallet_repository_port import WalletRepositoryPort
from app.domain.port.out.event_publisher_port import EventPublisherPort
from app.domain.exception.wallet_not_found_error import WalletNotFoundError
from app.domain.exception.insufficient_funds_error import InsufficientFundsError


class WithdrawCommandHandler(WithdrawUseCase):
    def __init__(
        self,
        repository: WalletRepositoryPort,
        event_publisher: EventPublisherPort,
    ):
        self._repo = repository
        self._publisher = event_publisher

    def execute(self, usuario_id: str, monto_creditos: float, cuenta_destino: str) -> SolicitudRetiro:
        wallet = self._repo.find_by_usuario_id(usuario_id)
        if not wallet:
            raise WalletNotFoundError(usuario_id)
        if not wallet.tiene_fondos(monto_creditos):
            raise InsufficientFundsError(usuario_id, wallet.saldo_creditos, monto_creditos)

        monto_usd = monto_creditos / wallet.tasa_cambio
        tx = Transaction(
            wallet_id=wallet.wallet_id,
            tipo=TipoTransaccion.RETIRO,
            monto=monto_usd,
            monto_creditos=monto_creditos,
            estado=EstadoTx.PENDIENTE,
            descripcion=f"Solicitud de retiro a cuenta {cuenta_destino}",
        )
        tx = self._repo.save_transaction(tx)

        solicitud = SolicitudRetiro(
            transaccion_id=tx.transaccion_id,
            cuenta_destino=cuenta_destino,
        )
        return self._repo.save_solicitud_retiro(solicitud)

    def ejecutar_retiro(self, solicitud_id: str) -> SolicitudRetiro:
        solicitud = self._repo.find_solicitud_by_id(solicitud_id)
        if not solicitud:
            raise ValueError(f"Solicitud no encontrada: {solicitud_id}")
        if solicitud.estado != EstadoTx.PENDIENTE:
            raise ValueError("Solicitud ya procesada")

        tx = self._repo.find_transaction_by_id(solicitud.transaccion_id)
        wallet = self._repo.find_by_wallet_id(tx.wallet_id)

        wallet.debitar(tx.monto_creditos)
        self._repo.save_wallet(wallet)

        solicitud.estado = EstadoTx.COMPLETADA
        solicitud.fecha_ejecucion = datetime.utcnow()
        tx.estado = EstadoTx.COMPLETADA
        self._repo.save_transaction(tx)
        return self._repo.save_solicitud_retiro(solicitud)
