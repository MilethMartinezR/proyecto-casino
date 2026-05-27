#!/bin/bash
set -e

mkdir -p /data/db /opt/kafka/data

# Start MySQL
service mysql start

# Initialize MySQL user/database if needed
if [ ! -d /var/lib/mysql/mysql ]; then
  echo "Initializing MySQL database..."
  mysqld --initialize-insecure --datadir=/var/lib/mysql
  service mysql restart
  mysql -uroot -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}'; FLUSH PRIVILEGES;"
  mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" -e "CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE}; CREATE USER IF NOT EXISTS '${MYSQL_USER}'@'%' IDENTIFIED BY '${MYSQL_PASSWORD}'; GRANT ALL PRIVILEGES ON ${MYSQL_DATABASE}.* TO '${MYSQL_USER}'@'%'; FLUSH PRIVILEGES;"
fi

# Start MongoDB
if [ ! -f /data/db/mongod.lock ]; then
  mongod --dbpath /data/db --bind_ip_all --fork --logpath /var/log/mongod.log --auth
  sleep 5
  mongo admin --eval "db.createUser({user:'${MONGO_INITDB_ROOT_USERNAME}',pwd:'${MONGO_INITDB_ROOT_PASSWORD}',roles:[{role:'root',db:'admin'}]})" || true
else
  mongod --dbpath /data/db --bind_ip_all --fork --logpath /var/log/mongod.log --auth || true
fi

# Start ZooKeeper
/opt/kafka/bin/zookeeper-server-start.sh -daemon /opt/kafka/config/zookeeper.properties
sleep 5

# Configure Kafka broker
cat > /opt/kafka/config/server.properties <<EOF
broker.id=${KAFKA_BROKER_ID}
listeners=${KAFKA_LISTENERS}
advertised.listeners=${KAFKA_ADVERTISED_LISTENERS}
zookeeper.connect=${KAFKA_ZOOKEEPER_CONNECT}
log.dirs=/opt/kafka/data
EOF

# Start Kafka
/opt/kafka/bin/kafka-server-start.sh -daemon /opt/kafka/config/server.properties
sleep 5

echo "Infra services started. Tailing logs..."
exec tail -F /var/log/mongod.log /var/log/mysql/error.log || true
