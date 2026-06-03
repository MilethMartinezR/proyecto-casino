const { Kafka } = require('kafkajs');

let producer = null;

async function connectKafka() {
  const kafka = new Kafka({
    clientId: 'game-service',
    brokers: (process.env.KAFKA_BOOTSTRAP_SERVERS ?? 'kafka:9092').split(','),
  });
  producer = kafka.producer();
  await producer.connect();
  console.log('[Kafka] Producer conectado');
  return producer;
}

function getProducer() {
  if (!producer) throw new Error('Kafka producer no inicializado');
  return producer;
}

module.exports = { connectKafka, getProducer };
