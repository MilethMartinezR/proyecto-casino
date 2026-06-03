class UserNotFoundError(Exception):
    def __init__(self, usuario_id: str):
        super().__init__(f"Usuario no encontrado: usuario_id={usuario_id}")
        self.usuario_id = usuario_id
