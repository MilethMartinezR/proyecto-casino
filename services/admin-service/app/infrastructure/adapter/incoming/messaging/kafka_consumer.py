import asyncio
import json
import logging
from aiokafka import AIOKafkaConsumer
from sqlalchemy.orm import Session

from app.infrastructure.config.settings import settings
from app.infrastructure.adapter.out.persistence.database import SessionLocal
from app.infrastructure.adapter.out.persistence.admin_repository_adapter import AdminRepositoryAdapter
from app.domain.model.admin_user import AdminUser, EstadoUsuario

logger = logging.getLogger(__name__)

TOPICS = ["auth-events", "wallet-events"]
GROUP_ID = "admin-service-group"


def _upsert_usuario(data: dict, db: Session):
    event_type = data.get("event_type", "")
    usuario_id = data.get("usuario_id")
    if not usuario_id:
        return

    repo = AdminRepositoryAdapter(db)
    user = repo.find_user_by_id(usuario_id)

    if event_type == "USUARIO_REGISTRADO":
        if not user:
            user = AdminUser(
                usuario_id=usuario_id,
                nombre=data.get("nombre", ""),
                email=data.get("email", ""),
                estado=EstadoUsuario.PENDIENTE_VERIFICACION,
                mfa_habilitado=data.get("mfa_habilitado", False),
            )
            repo.save_user(user)

    elif event_type in ("USUARIO_ACTIVADO", "MFA_VERIFICADO"):
        if user:
            user.activar()
            repo.save_user(user)

    elif event_type == "USUARIO_SUSPENDIDO":
        if user:
            user.suspender()
            repo.save_user(user)


async def _process_message(topic: str, data: dict, db: Session):
    if topic == "auth-events":
        _upsert_usuario(data, db)
    # wallet-events no requieren persistencia aquí,
    # se consultan directamente en la generación de reportes


async def start_consumer():
    consumer = AIOKafkaConsumer(
        *TOPICS,
        bootstrap_servers=settings.kafka_bootstrap_servers,
        group_id=GROUP_ID,
        value_deserializer=lambda m: json.loads(m.decode("utf-8")),
        auto_offset_reset="earliest",
    )
    await consumer.start()
    logger.info("Kafka consumer started on topics: %s", TOPICS)
    try:
        async for msg in consumer:
            db = SessionLocal()
            try:
                await _process_message(msg.topic, msg.value, db)
            except Exception as e:
                logger.error("Error processing message [%s]: %s", msg.topic, e)
                db.rollback()
            finally:
                db.close()
    finally:
        await consumer.stop()
