package co.casino.auth.domain.port.out;

import co.casino.auth.domain.model.User;

public interface AuthEventPublisherPort {
    void publishUserRegistered(User user);
    void publishUserLoggedIn(User user);
}