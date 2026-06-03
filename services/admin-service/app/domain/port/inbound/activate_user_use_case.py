from abc import ABC, abstractmethod
from app.domain.model.admin_user import AdminUser


class ActivateUserUseCase(ABC):
    @abstractmethod
    async def execute(self, usuario_id: str, admin_id: str) -> AdminUser:
        ...
