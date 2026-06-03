class UnauthorizedError(Exception):
    def __init__(self, message: str = "Acceso no autorizado"):
        super().__init__(message)
