from typing import Optional, List
from sqlalchemy.orm import Session

from app.domain.model.wallet import Wallet
from app.domain.model.transaction import Transaction, SolicitudRetiro, TipoTransaccion, EstadoTx
from app.domain.port.out.wallet_repository_port import WalletRepositoryPort
from app.infrastructure.adapter.out.persistence.wallet_model import WalletModel
from app.infrastructure.adapter.out.persistence.transaction_model import TransactionModel, SolicitudRetiroModel


def _to_wallet_domain(m: WalletModel) -> Wallet:
    return Wallet(
        wallet_id=m.wallet_id,
        usuario_id=m.usuario_id,
        saldo_creditos=m.saldo_creditos,
        tasa_cambio=m.tasa_cambio,
        fecha_actualizacion=m.fecha_actualizacion,
    )


def _to_transaction_domain(m: TransactionModel) -> Transaction:
    return Transaction(
        transaccion_id=m.transaccion_id,
        wallet_id=m.wallet_id,
        tipo=m.tipo,
        monto=m.monto,
        monto_creditos=m.monto_creditos,
        estado=m.estado,
        fecha=m.fecha,
        descripcion=m.descripcion,
    )


def _to_solicitud_domain(m: SolicitudRetiroModel) -> SolicitudRetiro:
    return SolicitudRetiro(
        solicitud_id=m.solicitud_id,
        transaccion_id=m.transaccion_id,
        cuenta_destino=m.cuenta_destino,
        estado=m.estado,
        fecha_solicitud=m.fecha_solicitud,
        fecha_ejecucion=m.fecha_ejecucion,
    )


class WalletRepositoryAdapter(WalletRepositoryPort):
    def __init__(self, db: Session):
        self._db = db

    def find_by_usuario_id(self, usuario_id: str) -> Optional[Wallet]:
        m = self._db.query(WalletModel).filter(WalletModel.usuario_id == usuario_id).first()
        return _to_wallet_domain(m) if m else None

    def find_by_wallet_id(self, wallet_id: str) -> Optional[Wallet]:
        m = self._db.query(WalletModel).filter(WalletModel.wallet_id == wallet_id).first()
        return _to_wallet_domain(m) if m else None

    def save_wallet(self, wallet: Wallet) -> Wallet:
        m = self._db.query(WalletModel).filter(WalletModel.wallet_id == wallet.wallet_id).first()
        if m:
            m.saldo_creditos = wallet.saldo_creditos
            m.tasa_cambio = wallet.tasa_cambio
        else:
            m = WalletModel(
                wallet_id=wallet.wallet_id,
                usuario_id=wallet.usuario_id,
                saldo_creditos=wallet.saldo_creditos,
                tasa_cambio=wallet.tasa_cambio,
            )
            self._db.add(m)
        self._db.commit()
        self._db.refresh(m)
        return _to_wallet_domain(m)

    def save_transaction(self, tx: Transaction) -> Transaction:
        m = self._db.query(TransactionModel).filter(TransactionModel.transaccion_id == tx.transaccion_id).first()
        if m:
            m.estado = tx.estado
        else:
            m = TransactionModel(
                transaccion_id=tx.transaccion_id,
                wallet_id=tx.wallet_id,
                tipo=tx.tipo,
                monto=tx.monto,
                monto_creditos=tx.monto_creditos,
                estado=tx.estado,
                descripcion=tx.descripcion,
            )
            self._db.add(m)
        self._db.commit()
        self._db.refresh(m)
        return _to_transaction_domain(m)

    def save_solicitud_retiro(self, solicitud: SolicitudRetiro) -> SolicitudRetiro:
        m = self._db.query(SolicitudRetiroModel).filter(
            SolicitudRetiroModel.solicitud_id == solicitud.solicitud_id
        ).first()
        if m:
            m.estado = solicitud.estado
            m.fecha_ejecucion = solicitud.fecha_ejecucion
        else:
            m = SolicitudRetiroModel(
                solicitud_id=solicitud.solicitud_id,
                transaccion_id=solicitud.transaccion_id,
                cuenta_destino=solicitud.cuenta_destino,
                estado=solicitud.estado,
            )
            self._db.add(m)
        self._db.commit()
        self._db.refresh(m)
        return _to_solicitud_domain(m)

    def find_solicitud_by_id(self, solicitud_id: str) -> Optional[SolicitudRetiro]:
        m = self._db.query(SolicitudRetiroModel).filter(
            SolicitudRetiroModel.solicitud_id == solicitud_id
        ).first()
        return _to_solicitud_domain(m) if m else None

    def find_transactions_by_wallet(self, wallet_id: str) -> List[Transaction]:
        rows = self._db.query(TransactionModel).filter(
            TransactionModel.wallet_id == wallet_id
        ).order_by(TransactionModel.fecha.desc()).all()
        return [_to_transaction_domain(r) for r in rows]

    def find_transaction_by_id(self, transaccion_id: str) -> Optional[Transaction]:
        m = self._db.query(TransactionModel).filter(
            TransactionModel.transaccion_id == transaccion_id
        ).first()
        return _to_transaction_domain(m) if m else None
