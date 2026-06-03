import logging
from contextlib import asynccontextmanager

import py_eureka_client.eureka_client as eureka_client
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.infrastructure.config.settings import settings
from app.infrastructure.adapter.out.persistence.database import engine
from app.infrastructure.adapter.out.persistence.wallet_model import WalletModel
from app.infrastructure.adapter.out.persistence.transaction_model import TransactionModel, SolicitudRetiroModel
from app.infrastructure.adapter.out.messaging.kafka_event_publisher import stop_producer
from app.infrastructure.adapter.incoming.rest.wallet_router import router as wallet_router

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Creating database tables...")
    from app.infrastructure.adapter.out.persistence.database import Base
    Base.metadata.create_all(bind=engine)

    logger.info("Registering with Eureka...")
    await eureka_client.init_async(
        eureka_server=settings.eureka_server_url,
        app_name=settings.service_name,
        instance_port=settings.service_port,
        instance_host="wallet-service",
    )

    yield

    await stop_producer()
    await eureka_client.stop_async()
    logger.info("wallet-service shut down cleanly")


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
