import os
from typing import Optional

from fastapi import Depends, HTTPException, Request, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from jose import JWTError, jwt
from app.infrastructure.config.settings import settings

bearer_scheme = HTTPBearer(auto_error=False)

# Clave compartida para llamadas internas entre servicios (game-service → wallet-service)
INTERNAL_SERVICE_KEY = os.getenv("INTERNAL_SERVICE_KEY", "casino_internal_service_key_2025")


def verify_token(
    request: Request,
    credentials: Optional[HTTPAuthorizationCredentials] = Depends(bearer_scheme),
) -> dict:
    # Bypass para llamadas internas entre servicios
    if request.headers.get("X-Service-Token") == INTERNAL_SERVICE_KEY:
        user_id = request.headers.get("X-User-Id", "service")
        return {"sub": user_id, "role": "SERVICE"}

    if credentials is None:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authenticated",
        )
    try:
        payload = jwt.decode(
            credentials.credentials,
            settings.jwt_secret,
            algorithms=[settings.jwt_algorithm],
        )
        return payload
    except JWTError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token inválido o expirado",
        )


def require_admin(payload: dict = Depends(verify_token)) -> dict:
    if payload.get("rol") != "ADMIN":
        raise HTTPException(status_code=403, detail="Acceso restringido a administradores")
    return payload
