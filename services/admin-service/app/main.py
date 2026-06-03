import asyncio
import logging
from contextlib import asynccontextmanager

import py_eureka_client.eureka_client as eureka_client
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.infrastructure.config.settings import settings
from app.infrastructure.adapter.out.persistence.database import engine, Base
from app.infrastructure.adapter.out.persistence.admin_model import UsuarioSnapshotModel
from app.infrastructure.adapter.out.persistence.report_model import ReporteModel
from app.infrastructure.adapter.out.messaging.kafka_event_publisher import stop_producer
from app.infrastructure.adapter.incoming.rest.admin_router import router as admin_router
from app.infrastructure.adapter.incoming.messaging.kafka_consumer import start_consumer

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Creating database tables...")
    Base.metadata.create_all(bind=engine)

    if settings.eureka_server_url:
        logger.info("Registering with Eureka...")
        await eureka_client.init_async(
            eureka_server=settings.eureka_server_url,
            app_name=settings.service_name,
            instance_port=settings.service_port,
            instance_host="admin-service",
        )

    # Arranca el consumer de Kafka en background
    consumer_task = asyncio.create_task(start_consumer())
    logger.info("Kafka consumer task started")

    yield

    consumer_task.cancel()
    try:
        await consumer_task
    except asyncio.CancelledError:
        pass

    await stop_producer()
    if settings.eureka_server_url:
        await eureka_client.stop_async()
    logger.info("admin-service shut down cleanly")


app = FastAPI(
    title="Admin Service",
    description="Panel de administración y reportes financieros — Casino Online",
    version="1.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(admin_router)


@app.get("/health")
def health():
    return {"status": "UP", "service": settings.service_name}
