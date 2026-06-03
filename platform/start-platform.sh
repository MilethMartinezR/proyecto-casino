#!/bin/bash
set -e

mkdir -p /data/db /opt/kafka/data /var/log

# ── Redis ──────────────────────────────────────────────────────────────────
redis-server --daemonize yes --bind 0.0.0.0 --loglevel notice
echo "[platform] Redis listo"

# ── MySQL ──────────────────────────────────────────────────────────────────
service mysql start || true
sleep 3

# Primera vez: inicializar usuario y base de datos
if ! mysql -uroot -e "SELECT 1" 2>/dev/null; then
  mysqld --initialize-insecure --datadir=/var/lib/mysql
  service mysql restart
  sleep 3
fi

mysql -uroot <<SQL
  SET GLOBAL validate_password.policy = LOW;
  ALTER USER 'root'@'localhost' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}';
  CREATE DATABASE IF NOT EXISTS \`${MYSQL_DATABASE}\`;
  CREATE USER IF NOT EXISTS '${MYSQL_USER}'@'%' IDENTIFIED BY '${MYSQL_PASSWORD}';
  GRANT ALL PRIVILEGES ON \`${MYSQL_DATABASE}\`.* TO '${MYSQL_USER}'@'%';
  FLUSH PRIVILEGES;
SQL

# Ejecutar scripts de inicialización
for f in /platform/init-scripts/mysql/*.sql; do
  echo "[platform] Ejecutando $f ..."
  mysql -u"${MYSQL_USER}" -p"${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" < "$f" || true
done
echo "[platform] MySQL listo"

# ── MongoDB ────────────────────────────────────────────────────────────────
if [ ! -d /data/db/journal ]; then
  mongod --dbpath /data/db --bind_ip_all --fork --logpath /var/log/mongod.log
  sleep 5
  mongosh admin --eval "
    db.createUser({
      user: '${MONGO_INITDB_ROOT_USERNAME}',
      pwd:  '${MONGO_INITDB_ROOT_PASSWORD}',
      roles: [{role:'root', db:'admin'}]
    })
  " || true
  mongod --dbpath /data/db --shutdown || true
  sleep 2
fi
mongod --dbpath /data/db --bind_ip_all --auth --fork --logpath /var/log/mongod.log
echo "[platform] MongoDB listo"

# ── ZooKeeper ──────────────────────────────────────────────────────────────
/opt/kafka/bin/zookeeper-server-start.sh -daemon /opt/kafka/config/zookeeper.properties
sleep 6

# ── Kafka ──────────────────────────────────────────────────────────────────
cat > /opt/kafka/config/server.properties <<EOF
broker.id=${KAFKA_BROKER_ID:-1}
listeners=${KAFKA_LISTENERS:-PLAINTEXT://0.0.0.0:9092}
advertised.listeners=${KAFKA_ADVERTISED_LISTENERS:-PLAINTEXT://platform:9092}
zookeeper.connect=localhost:2181
log.dirs=/opt/kafka/data
auto.create.topics.enable=false
num.partitions=3
default.replication.factor=1
EOF

/opt/kafka/bin/kafka-server-start.sh -daemon /opt/kafka/config/server.properties
sleep 8

# Crear topics
for topic in game-events wallet-events user-events audit-events admin-events; do
  /opt/kafka/bin/kafka-topics.sh \
    --create --bootstrap-server localhost:9092 \
    --topic "$topic" --partitions 3 --replication-factor 1 \
    --if-not-exists
done
echo "[platform] Kafka topics creados"

# ── Eureka Server ──────────────────────────────────────────────────────────
java -jar /opt/eureka-server.jar \
  --server.port=8761 \
  --spring.application.name=eureka-server \
  --eureka.client.register-with-eureka=false \
  --eureka.client.fetch-registry=false &
echo "[platform] Eureka arrancando en puerto 8761..."

echo "[platform] Todos los servicios de plataforma iniciados."
exec tail -F /var/log/mongod.log
