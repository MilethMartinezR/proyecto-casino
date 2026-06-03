package co.casino.auth.infrastructure.adapter.out.messaging.kafka.producer;

import co.casino.auth.domain.model.User;
import co.casino.auth.domain.port.out.AuthEventPublisherPort;
import co.casino.auth.infrastructure.adapter.out.messaging.kafka.event.UserLoggedInEvent;
import co.casino.auth.infrastructure.adapter.out.messaging.kafka.event.UserRegisteredEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuthEventProducer implements AuthEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.auth-events}")
    private String authEventsTopic;

    public AuthEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishUserRegistered(User user) {
        kafkaTemplate.send(authEventsTopic, user.getId(),
            new UserRegisteredEvent(user.getId(), user.getUsername(), user.getEmail(),
                                    user.getRole(), user.isMfaEnabled()));
    }

    @Override
    public void publishUserLoggedIn(User user) {
        kafkaTemplate.send(authEventsTopic, user.getId(),
            new UserLoggedInEvent(user.getId(), user.getEmail()));
    }
}
