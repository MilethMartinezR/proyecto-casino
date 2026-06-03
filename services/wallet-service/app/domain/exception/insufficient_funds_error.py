class InsufficientFundsError(Exception):
    def __init__(self, usuario_id: str, saldo: float, requerido: float):
        super().__init__(
            f"Saldo insuficiente para usuario_id={usuario_id}: "
            f"disponible={saldo}, requerido={requerido}"
        )
        self.usuario_id = usuario_id
        self.saldo = saldo
        self.requerido = requerido
