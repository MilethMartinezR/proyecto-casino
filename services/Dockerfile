FROM ubuntu:22.04
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y --no-install-recommends \
      openjdk-17-jdk maven \
      python3 python3-pip \
      curl netcat-openbsd \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y --no-install-recommends nodejs \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY services/auth-service   ./auth-service
COPY services/audit-service  ./audit-service
COPY services/wallet-service ./wallet-service
COPY services/admin-service  ./admin-service
COPY services/game-service   ./game-service
COPY services/start-services.sh /usr/local/bin/start-services.sh
RUN chmod +x /usr/local/bin/start-services.sh

RUN if [ -f /app/wallet-service/requirements.txt ]; then pip3 install -r /app/wallet-service/requirements.txt; fi
RUN if [ -f /app/admin-service/requirements.txt ]; then pip3 install -r /app/admin-service/requirements.txt; fi
RUN if [ -f /app/game-service/package.json ]; then cd /app/game-service && npm install; fi

EXPOSE 8081 8082 8083 8084 8085

CMD ["/usr/local/bin/start-services.sh"]
