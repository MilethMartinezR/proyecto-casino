from typing import Optional
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session

from app.application.dto.admin_schemas import UsuarioSnapshotResponse, CambioEstadoRequest
from app.application.dto.report_schemas import ReporteRequest, ReporteResponse
from app.application.usecase.list_users_use_case_impl import ListUsersUseCaseImpl
from app.application.usecase.suspend_user_use_case_impl import SuspendUserUseCaseImpl
from app.application.usecase.activate_user_use_case_impl import ActivateUserUseCaseImpl
from app.application.usecase.generate_report_use_case_impl import GenerateReportUseCaseImpl
from app.domain.model.admin_user import EstadoUsuario
from app.domain.exception.user_not_found_error import UserNotFoundError
from app.infrastructure.adapter.incoming.rest.auth_middleware import require_admin
from app.infrastructure.adapter.out.persistence.database import get_db
from app.infrastructure.adapter.out.persistence.admin_repository_adapter import AdminRepositoryAdapter
from app.infrastructure.adapter.out.messaging.kafka_event_publisher import KafkaEventPublisher

router = APIRouter(prefix="/admin", tags=["admin"])


def _repo(db: Session) -> AdminRepositoryAdapter:
    return AdminRepositoryAdapter(db)


# ── Usuarios ──────────────────────────────────────────────────────

@router.get("/users", response_model=list[UsuarioSnapshotResponse])
def listar_usuarios(
    estado: Optional[EstadoUsuario] = Query(None),
    skip: int = Query(0, ge=0),
    limit: int = Query(50, le=200),
    db: Session = Depends(get_db),
    _admin: dict = Depends(require_admin),
):
    return ListUsersUseCaseImpl(_repo(db)).execute(estado, skip, limit)


@router.get("/users/{usuario_id}", response_model=UsuarioSnapshotResponse)
def get_usuario(
    usuario_id: str,
    db: Session = Depends(get_db),
    _admin: dict = Depends(require_admin),
):
    user = _repo(db).find_user_by_id(usuario_id)
    if not user:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    return user


@router.put("/users/{usuario_id}/suspend", response_model=UsuarioSnapshotResponse)
async def suspender_usuario(
    usuario_id: str,
    req: CambioEstadoRequest,
    db: Session = Depends(get_db),
    _admin: dict = Depends(require_admin),
):
    try:
        return await SuspendUserUseCaseImpl(_repo(db), KafkaEventPublisher()).execute(
            usuario_id, req.admin_id
        )
    except UserNotFoundError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.put("/users/{usuario_id}/activate", response_model=UsuarioSnapshotResponse)
async def activar_usuario(
    usuario_id: str,
    req: CambioEstadoRequest,
    db: Session = Depends(get_db),
    _admin: dict = Depends(require_admin),
):
    try:
        return await ActivateUserUseCaseImpl(_repo(db), KafkaEventPublisher()).execute(
            usuario_id, req.admin_id
        )
    except UserNotFoundError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


# ── Reportes ──────────────────────────────────────────────────────

@router.post("/reports", response_model=ReporteResponse, status_code=201)
def generar_reporte(
    req: ReporteRequest,
    db: Session = Depends(get_db),
    _admin: dict = Depends(require_admin),
):
    return GenerateReportUseCaseImpl(_repo(db)).execute(
        req.admin_id, req.tipo, req.periodo_inicio, req.periodo_fin
    )


@router.get("/reports", response_model=list[ReporteResponse])
def listar_reportes(
    admin_id: str = Query(...),
    db: Session = Depends(get_db),
    _admin: dict = Depends(require_admin),
):
    return _repo(db).list_reports_by_admin(admin_id)


@router.get("/reports/{reporte_id}", response_model=ReporteResponse)
def get_reporte(
    reporte_id: str,
    db: Session = Depends(get_db),
    _admin: dict = Depends(require_admin),
):
    report = _repo(db).find_report_by_id(reporte_id)
    if not report:
        raise HTTPException(status_code=404, detail="Reporte no encontrado")
    return report
