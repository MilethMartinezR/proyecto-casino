import asyncio
import json
import logging
from aiokafka import AIOKafkaConsumer

from app.infrastructure.config.settings import settings
from app.infrastructure.adapter.out.persistence.database import SessionLocal
from app.infrastructure.adapter.out.persistence.wallet_repository_adapter import WalletRepositoryAdapter
from app.infrastructure.adapter.out.messaging.kafka_event_publisher import KafkaEventPublisher
from app.application.command.credit_game_winnings_command_handler import CreditGameWinningsCommandHandler

logger = logging.getLogger(__name__)


async def start_game_events_consumer() -> None:
    consumer = AIOKafkaConsumer(
        "game-events",
        bootstrap_servers=settings.kafka_bootstrap_servers,
        group_id="wallet-service-group",
        auto_offset_reset="earliest",
        value_deserializer=lambda m: json.loads(m.decode("utf-8")),
    )
    await consumer.start()
    logger.info("[Kafka] Consumer de game-events iniciado")

    try:
        async for msg in consumer:
            await _handle(msg.value)
    except asyncio.CancelledError:
        pass
    finally:
        await consumer.stop()
        logger.info("[Kafka] Consumer de game-events detenido")


async def _handle(event: dict) -> None:
    event_type = event.get("eventType")
    pago = event.get("pago", 0)

    if event_type != "GAME_FINISHED" or pago <= 0:
        return

    usuario_id = event.get("userId")
    game_id = event.get("gameId")

    try:
        db = SessionLocal()
        try:
            handler = CreditGameWinningsCommandHandler(
                WalletRepositoryAdapter(db),
                KafkaEventPublisher(),
            )
            await handler.execute(usuario_id, pago, game_id)
            logger.info("[wallet] Ganancia acreditada: %s créditos → usuario %s", pago, usuario_id)
        finally:
            db.close()
    except Exception as e:
        logger.error("[wallet] Error acreditando ganancia de partida %s: %s", game_id, e)
