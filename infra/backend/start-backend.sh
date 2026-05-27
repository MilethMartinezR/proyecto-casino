#!/bin/bash
set -e

started=false

echo "Waiting for infra availability..."
while ! nc -z infra 3306 || ! nc -z infra 27017 || ! nc -z infra 9092; do
  echo "Waiting for infra services..."
  sleep 3
done

echo "Infra is available. Starting backend services..."

if [ -f /app/auth-service/pom.xml ]; then
  cd /app/auth-service
  mvn -q package -DskipTests
  java -jar target/*.jar &
  started=true
fi

if [ -f /app/audit-service/pom.xml ]; then
  cd /app/audit-service
  mvn -q package -DskipTests
  java -jar target/*.jar &
  started=true
fi

if [ -f /app/wallet-service/requirements.txt ]; then
  cd /app/wallet-service
  python3 -m uvicorn app.main:app --host 0.0.0.0 --port 8082 &
  started=true
fi

if [ -f /app/admin-service/requirements.txt ]; then
  cd /app/admin-service
  python3 -m uvicorn app.main:app --host 0.0.0.0 --port 8085 &
  started=true
fi

if [ -f /app/game-service/src/server.js ]; then
  cd /app/game-service
  node src/server.js &
  started=true
elif [ -f /app/game-service/server.js ]; then
  cd /app/game-service
  node server.js &
  started=true
fi

if [ "$started" = false ]; then
  echo "No backend processes were started. Sleeping so the container remains alive."
  sleep infinity
else
  echo "Backend coordinator is running."
  wait
fi
