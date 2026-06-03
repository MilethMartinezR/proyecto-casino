from dataclasses import dataclass


@dataclass
class GetTransactionsQuery:
    usuario_id: str
