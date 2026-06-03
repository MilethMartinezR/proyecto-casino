const { EventPublisherPort } = require('../../../../domain/port/out/eventPublisherPort');
const { getProducer } = require('../../../config/kafka');

class KafkaEventPublisher extends EventPublisherPort {
  async publish(topic, event) {
    const producer = getProducer();
    await producer.send({
      topic,
      messages: [{
        key: event.gameId ?? event.userId,
        value: JSON.stringify(event),
      }],
    });
  }
}

module.exports = { KafkaEventPublisher };
