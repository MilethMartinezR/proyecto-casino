import asyncio
import logging
from contextlib import asynccontextmanager

import py_eureka_client.eureka_client as eureka_client
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.infrastructure.config.settings import settings
from app.infrastructure.adapter.out.messaging.kafka_event_publisher import stop_producer
from app.infrastructure.adapter.incoming.messaging.kafka_consumer import start_game_events_consumer, start_auth_events_consumer
from app.infrastructure.adapter.incoming.rest.wallet_router import router as wallet_router

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

_consumer_task: asyncio.Task | None = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    global _consumer_task

    logger.info("[wallet-service] Registrando en Eureka...")
    await eureka_client.init_async(
        eureka_server=settings.eureka_server_url,
        app_name=settings.service_name,
        instance_port=settings.service_port,
        instance_host="wallet-service",
    )

    logger.info("[wallet-service] Iniciando consumer de Kafka...")
    _consumer_task = asyncio.create_task(start_game_events_consumer())
    asyncio.create_task(start_auth_events_consumer())

    yield

    if _consumer_task:
        _consumer_task.cancel()
        await asyncio.gather(_consumer_task, return_exceptions=True)

    await stop_producer()
    await eureka_client.stop_async()
    logger.info("[wallet-service] Apagado limpio")


app = FastAPI(
    title="Wallet Service",
    description="Gestión de créditos, depósitos y retiros — Casino Online",
    version="1.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(wallet_router)


@app.get("/health")
def health():
    return {"status": "UP", "service": settings.service_name}
