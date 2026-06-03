class WalletNotFoundError(Exception):
    def __init__(self, usuario_id: str):
        super().__init__(f"Wallet no encontrada para usuario_id={usuario_id}")
        self.usuario_id = usuario_id
