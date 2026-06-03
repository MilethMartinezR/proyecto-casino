from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.application.command.deposit_command_handler import DepositCommandHandler
from app.application.command.withdraw_command_handler import WithdrawCommandHandler
from app.application.query.get_balance_query_handler import GetBalanceQueryHandler
from app.application.query.get_transactions_query_handler import GetTransactionsQueryHandler
from app.application.dto.wallet_schemas import WalletResponse, DepositRequest, WithdrawRequest, CobrarApuestaRequest
from app.application.dto.transaction_schemas import TransaccionResponse, SolicitudRetiroResponse
from app.infrastructure.adapter.incoming.rest.auth_middleware import verify_token
from app.infrastructure.adapter.out.persistence.database import get_db
from app.infrastructure.adapter.out.persistence.wallet_repository_adapter import WalletRepositoryAdapter
from app.infrastructure.adapter.out.messaging.kafka_event_publisher import KafkaEventPublisher
from app.domain.exception.wallet_not_found_error import WalletNotFoundError
from app.domain.exception.insufficient_funds_error import InsufficientFundsError

router = APIRouter(prefix="/wallet", tags=["wallet"])


def _make_repo(db: Session) -> WalletRepositoryAdapter:
    return WalletRepositoryAdapter(db)


@router.get("/{usuario_id}", response_model=WalletResponse)
def consultar_saldo(
    usuario_id: str,
    db: Session = Depends(get_db),
    _token: dict = Depends(verify_token),
):
    try:
        return GetBalanceQueryHandler(_make_repo(db)).execute(usuario_id)
    except WalletNotFoundError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.post("/deposit", response_model=TransaccionResponse, status_code=201)
def depositar(
    req: DepositRequest,
    db: Session = Depends(get_db),
    _token: dict = Depends(verify_token),
):
    return DepositCommandHandler(_make_repo(db), KafkaEventPublisher()).execute(
        req.usuario_id, req.monto_usd
    )


@router.post("/withdraw", response_model=SolicitudRetiroResponse, status_code=202)
def solicitar_retiro(
    req: WithdrawRequest,
    db: Session = Depends(get_db),
    _token: dict = Depends(verify_token),
):
    try:
        return WithdrawCommandHandler(_make_repo(db), KafkaEventPublisher()).execute(
            req.usuario_id, req.monto_creditos, req.cuenta_destino
        )
    except WalletNotFoundError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except InsufficientFundsError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.put("/withdraw/{solicitud_id}/exec", response_model=SolicitudRetiroResponse)
def ejecutar_retiro(
    solicitud_id: str,
    db: Session = Depends(get_db),
    _token: dict = Depends(verify_token),
):
    try:
        return WithdrawCommandHandler(_make_repo(db), KafkaEventPublisher()).ejecutar_retiro(solicitud_id)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.post("/cobrar-apuesta", response_model=TransaccionResponse, status_code=201)
def cobrar_apuesta(
    req: CobrarApuestaRequest,
    db: Session = Depends(get_db),
    _token: dict = Depends(verify_token),
):
    """Endpoint interno: game-service cobra créditos por apuesta."""
    from app.domain.model.transaction import Transaction, TipoTransaccion, EstadoTx
    from app.domain.exception.wallet_not_found_error import WalletNotFoundError
    from app.domain.exception.insufficient_funds_error import InsufficientFundsError

    repo = _make_repo(db)
    wallet = repo.find_by_usuario_id(req.usuario_id)
    if not wallet:
        raise HTTPException(status_code=404, detail=f"Wallet no encontrada para usuario {req.usuario_id}")
    if not wallet.tiene_fondos(req.monto_creditos):
        raise HTTPException(status_code=422, detail="Saldo insuficiente para realizar la apuesta")

    wallet.debitar(req.monto_creditos)
    repo.save_wallet(wallet)

    tx = Transaction(
        wallet_id=wallet.wallet_id,
        tipo=TipoTransaccion.APUESTA,
        monto=req.monto_creditos / wallet.tasa_cambio,
        monto_creditos=req.monto_creditos,
        estado=EstadoTx.COMPLETADA,
        descripcion=req.descripcion or f"Apuesta: {req.monto_creditos} créditos",
    )
    return repo.save_transaction(tx)


@router.get("/transactions/{usuario_id}", response_model=list[TransaccionResponse])
def historial_transacciones(
    usuario_id: str,
    db: Session = Depends(get_db),
    _token: dict = Depends(verify_token),
):
    try:
        return GetTransactionsQueryHandler(_make_repo(db)).execute(usuario_id)
    except WalletNotFoundError as e:
        raise HTTPException(status_code=404, detail=str(e))
