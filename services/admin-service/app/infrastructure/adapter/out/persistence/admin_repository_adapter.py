from typing import Optional, List
from datetime import datetime
from sqlalchemy.orm import Session

from app.domain.model.admin_user import AdminUser, EstadoUsuario
from app.domain.model.report import Report
from app.domain.port.out.admin_repository_port import AdminRepositoryPort
from app.infrastructure.adapter.out.persistence.admin_model import UsuarioSnapshotModel, MovimientoJuegoModel
from app.infrastructure.adapter.out.persistence.report_model import ReporteModel


def _to_user_domain(m: UsuarioSnapshotModel) -> AdminUser:
    return AdminUser(
        usuario_id=m.usuario_id,
        nombre=m.nombre,
        email=m.email,
        estado=m.estado,
        mfa_habilitado=m.mfa_habilitado,
        fecha_registro=m.fecha_registro,
        fecha_actualizacion=m.fecha_actualizacion,
    )


def _to_report_domain(m: ReporteModel) -> Report:
    return Report(
        reporte_id=m.reporte_id,
        generado_por=m.generado_por,
        tipo=m.tipo,
        periodo_inicio=m.periodo_inicio,
        periodo_fin=m.periodo_fin,
        total_creditos_comprados=m.total_creditos_comprados,
        total_creditos_apostados=m.total_creditos_apostados,
        total_creditos_ganados=m.total_creditos_ganados,
        balance_global=m.balance_global,
        fecha_generacion=m.fecha_generacion,
    )


class AdminRepositoryAdapter(AdminRepositoryPort):
    def __init__(self, db: Session):
        self._db = db

    # ── Usuarios ──────────────────────────────────────────────────────────

    def find_user_by_id(self, usuario_id: str) -> Optional[AdminUser]:
        m = self._db.query(UsuarioSnapshotModel).filter(
            UsuarioSnapshotModel.usuario_id == usuario_id
        ).first()
        return _to_user_domain(m) if m else None

    def save_user(self, user: AdminUser) -> AdminUser:
        m = self._db.query(UsuarioSnapshotModel).filter(
            UsuarioSnapshotModel.usuario_id == user.usuario_id
        ).first()
        if m:
            m.estado = user.estado
            m.mfa_habilitado = user.mfa_habilitado
            m.fecha_actualizacion = user.fecha_actualizacion
        else:
            m = UsuarioSnapshotModel(
                usuario_id=user.usuario_id,
                nombre=user.nombre,
                email=user.email,
                estado=user.estado,
                mfa_habilitado=user.mfa_habilitado,
            )
            self._db.add(m)
        self._db.commit()
        self._db.refresh(m)
        return _to_user_domain(m)

    def list_users(self, estado: Optional[EstadoUsuario], skip: int, limit: int) -> List[AdminUser]:
        q = self._db.query(UsuarioSnapshotModel)
        if estado:
            q = q.filter(UsuarioSnapshotModel.estado == estado)
        rows = q.order_by(UsuarioSnapshotModel.fecha_registro.desc()).offset(skip).limit(limit).all()
        return [_to_user_domain(r) for r in rows]

    # ── Reportes ──────────────────────────────────────────────────────────

    def save_report(self, report: Report) -> Report:
        m = ReporteModel(
            reporte_id=report.reporte_id,
            generado_por=report.generado_por,
            tipo=report.tipo,
            periodo_inicio=report.periodo_inicio,
            periodo_fin=report.periodo_fin,
            total_creditos_comprados=report.total_creditos_comprados,
            total_creditos_apostados=report.total_creditos_apostados,
            total_creditos_ganados=report.total_creditos_ganados,
            balance_global=report.balance_global,
        )
        self._db.add(m)
        self._db.commit()
        self._db.refresh(m)
        return _to_report_domain(m)

    def list_reports_by_admin(self, admin_id: str) -> List[Report]:
        rows = self._db.query(ReporteModel).filter(
            ReporteModel.generado_por == admin_id
        ).order_by(ReporteModel.fecha_generacion.desc()).all()
        return [_to_report_domain(r) for r in rows]

    def find_report_by_id(self, reporte_id: str) -> Optional[Report]:
        m = self._db.query(ReporteModel).filter(
            ReporteModel.reporte_id == reporte_id
        ).first()
        return _to_report_domain(m) if m else None

    # ── Movimientos de juego (fuente propia vía game-events) ──────────────

    def save_movimiento(self, data: dict) -> None:
        m = MovimientoJuegoModel(
            usuario_id=data.get("usuario_id", ""),
            game_id=data.get("game_id", ""),
            event_type=data.get("event_type", ""),
            monto_apostado=data.get("monto_apostado", 0.0),
            monto_ganado=data.get("monto_ganado", 0.0),
        )
        self._db.add(m)
        self._db.commit()

    def query_transacciones(self, inicio: datetime, fin: datetime) -> dict:
        rows = self._db.query(MovimientoJuegoModel).filter(
            MovimientoJuegoModel.timestamp >= inicio,
            MovimientoJuegoModel.timestamp <= fin,
        ).all()

        apostados = sum(r.monto_apostado for r in rows)
        ganados   = sum(r.monto_ganado   for r in rows)

        return {
            "total_creditos_comprados": 0.0,   # requiere wallet-events cuando se implemente
            "total_creditos_apostados": apostados,
            "total_creditos_ganados":   ganados,
            "balance_global":           apostados - ganados,
        }
