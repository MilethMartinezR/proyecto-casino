#!/bin/bash
set -e

echo "[services] Esperando a que platform esté disponible..."
until nc -z mysql 3306 && nc -z mongodb 27017 && nc -z kafka 9092 && nc -z eureka 8761; do
  echo "[services] Platform no disponible, reintentando en 5s..."
  sleep 5
done
echo "[services] Platform listo. Iniciando microservicios..."

# auth-service — Spring Boot — 8081
if [ -f /app/auth-service/target/*.jar ]; then
  cd /app/auth-service && java -jar target/*.jar &
fi

# audit-service — Spring Boot — 8084
if [ -f /app/audit-service/target/*.jar ]; then
  cd /app/audit-service && java -jar target/*.jar &
fi

# wallet-service — FastAPI — 8082
if [ -f /app/wallet-service/requirements.txt ]; then
  cd /app/wallet-service && python3 -m uvicorn app.main:app --host 0.0.0.0 --port 8082 &
fi

# admin-service — FastAPI — 8085
if [ -f /app/admin-service/requirements.txt ]; then
  cd /app/admin-service && python3 -m uvicorn app.main:app --host 0.0.0.0 --port 8085 &
fi

# game-service — Node.js — 8083
if [ -f /app/game-service/src/server.js ]; then
  cd /app/game-service && node src/server.js &
fi

echo "[services] Todos los microservicios en marcha."
wait
