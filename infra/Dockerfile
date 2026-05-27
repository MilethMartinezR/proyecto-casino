FROM ubuntu:22.04
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       curl gnupg lsb-release software-properties-common apt-transport-https ca-certificates \
       wget netcat mysql-server mongodb openjdk-17-jre-headless procps \
    && rm -rf /var/lib/apt/lists/*

RUN wget -qO /tmp/kafka.tgz https://downloads.apache.org/kafka/3.5.1/kafka_2.13-3.5.1.tgz \
    && tar -xzf /tmp/kafka.tgz -C /opt \
    && mv /opt/kafka_2.13-3.5.1 /opt/kafka \
    && rm /tmp/kafka.tgz

COPY infra/start-infra.sh /usr/local/bin/start-infra.sh
RUN chmod +x /usr/local/bin/start-infra.sh

EXPOSE 27017 3306 9092 2181 6379

CMD ["/usr/local/bin/start-infra.sh"]
