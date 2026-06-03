import json
import logging
from aiokafka import AIOKafkaProducer
from app.domain.port.out.report_repository_port import EventPublisherPort
from app.infrastructure.config.settings import settings

logger = logging.getLogger(__name__)

_producer: AIOKafkaProducer | None = None


async def get_producer() -> AIOKafkaProducer:
    global _producer
    if _producer is None:
        _producer = AIOKafkaProducer(
            bootstrap_servers=settings.kafka_bootstrap_servers,
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
        )
        await _producer.start()
    return _producer


async def stop_producer():
    global _producer
    if _producer:
        await _producer.stop()
        _producer = None


class KafkaEventPublisher(EventPublisherPort):
    async def publish(self, event: dict, topic: str) -> None:
        try:
            producer = await get_producer()
            await producer.send_and_wait(topic, event)
            logger.info("Kafka event published → %s: %s", topic, event.get("event_type"))
        except Exception as e:
            logger.error("Failed to publish Kafka event: %s", e)
