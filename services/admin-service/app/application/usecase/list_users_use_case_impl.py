from typing import List, Optional
from app.domain.model.admin_user import AdminUser, EstadoUsuario
from app.domain.port.inbound.list_users_use_case import ListUsersUseCase
from app.domain.port.out.admin_repository_port import AdminRepositoryPort


class ListUsersUseCaseImpl(ListUsersUseCase):
    def __init__(self, repository: AdminRepositoryPort):
        self._repo = repository

    def execute(self, estado: Optional[EstadoUsuario], skip: int, limit: int) -> List[AdminUser]:
        return self._repo.list_users(estado, skip, limit)
