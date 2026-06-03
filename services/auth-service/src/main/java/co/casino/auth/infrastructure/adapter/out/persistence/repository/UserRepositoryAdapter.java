package co.casino.auth.infrastructure.adapter.out.persistence.repository;

import co.casino.auth.domain.model.User;
import co.casino.auth.domain.port.out.UserRepositoryPort;
import co.casino.auth.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpa;

    public UserRepositoryAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public User save(User user) {
        return toModel(jpa.save(toEntity(user)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email).map(this::toModel);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpa.findByUsername(username).map(this::toModel);
    }

    @Override
    public Optional<User> findById(String id) {
        return jpa.findById(id).map(this::toModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpa.existsByUsername(username);
    }

    private UserEntity toEntity(User u) {
        UserEntity e = new UserEntity();
        e.setId(u.getId());
        e.setUsername(u.getUsername());
        e.setEmail(u.getEmail());
        e.setPasswordHash(u.getPasswordHash());
        e.setMfaEnabled(u.isMfaEnabled());
        e.setMfaSecret(u.getMfaSecret());
        e.setRole(UserEntity.Role.valueOf(u.getRole()));
        e.setActive(!"SUSPENDED".equals(u.getStatus()));
        e.setCreatedAt(u.getCreatedAt());
        e.setUpdatedAt(u.getUpdatedAt());
        return e;
    }

    private User toModel(UserEntity e) {
        String status = e.isActive() ? "ACTIVE" : "SUSPENDED";
        return new User(e.getId(), e.getUsername(), e.getEmail(), e.getPasswordHash(),
                        e.isMfaEnabled(), e.getMfaSecret(), e.getRole().name(),
                        status, e.getCreatedAt(), e.getUpdatedAt());
    }
}
