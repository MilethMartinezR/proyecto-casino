from abc import ABC, abstractmethod
from typing import List, Optional
from app.domain.model.admin_user import AdminUser, EstadoUsuario


class ListUsersUseCase(ABC):
    @abstractmethod
    def execute(self, estado: Optional[EstadoUsuario], skip: int, limit: int) -> List[AdminUser]:
        ...
