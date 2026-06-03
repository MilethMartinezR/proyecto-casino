from datetime import datetime
from app.domain.model.admin_user import AdminUser, EstadoUsuario
from app.domain.port.inbound.activate_user_use_case import ActivateUserUseCase
from app.domain.port.out.admin_repository_port import AdminRepositoryPort
from app.domain.port.out.report_repository_port import EventPublisherPort
from app.domain.exception.user_not_found_error import UserNotFoundError

TOPIC_ADMIN_EVENTS = "admin-events"


class ActivateUserUseCaseImpl(ActivateUserUseCase):
    def __init__(self, repository: AdminRepositoryPort, publisher: EventPublisherPort):
        self._repo = repository
        self._publisher = publisher

    async def execute(self, usuario_id: str, admin_id: str) -> AdminUser:
        user = self._repo.find_user_by_id(usuario_id)
        if not user:
            raise UserNotFoundError(usuario_id)
        if user.estado == EstadoUsuario.ACTIVO:
            raise ValueError("El usuario ya está activo")

        user.activar()
        saved = self._repo.save_user(user)

        await self._publisher.publish({
            "event_type": "USUARIO_ACTIVADO",
            "usuario_id": usuario_id,
            "admin_id": admin_id,
            "nuevo_estado": EstadoUsuario.ACTIVO,
            "timestamp": datetime.utcnow().isoformat(),
        }, TOPIC_ADMIN_EVENTS)

        return saved
