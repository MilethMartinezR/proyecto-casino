package co.casino.auth.domain.port.out;

import co.casino.auth.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findById(String id);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}