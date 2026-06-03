from abc import ABC, abstractmethod
from typing import Optional, List
from app.domain.model.admin_user import AdminUser, EstadoUsuario
from app.domain.model.report import Report


class AdminRepositoryPort(ABC):
    @abstractmethod
    def find_user_by_id(self, usuario_id: str) -> Optional[AdminUser]:
        ...

    @abstractmethod
    def save_user(self, user: AdminUser) -> AdminUser:
        ...

    @abstractmethod
    def list_users(self, estado: Optional[EstadoUsuario], skip: int, limit: int) -> List[AdminUser]:
        ...

    @abstractmethod
    def save_report(self, report: Report) -> Report:
        ...

    @abstractmethod
    def list_reports_by_admin(self, admin_id: str) -> List[Report]:
        ...

    @abstractmethod
    def find_report_by_id(self, reporte_id: str) -> Optional[Report]:
        ...

    @abstractmethod
    def query_transacciones(self, inicio, fin) -> dict:
        ...
