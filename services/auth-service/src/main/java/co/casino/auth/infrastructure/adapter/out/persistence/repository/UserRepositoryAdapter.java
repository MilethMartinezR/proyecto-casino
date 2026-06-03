package co.casino.auth.infrastructure.adapter.out.persistence.repository;

import co.casino.auth.domain.model.User;
import co.casino.auth.domain.port.out.UserRepositoryPort;
import co.casino.auth.infrastructure.adapter.out.persistence.document.UserDocument;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserMongoRepository mongo;

    public UserRepositoryAdapter(UserMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public User save(User user) {
        return toModel(mongo.save(toDocument(user)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return mongo.findByEmail(email).map(this::toModel);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return mongo.findByUsername(username).map(this::toModel);
    }

    @Override
    public Optional<User> findById(String id) {
        return mongo.findById(id).map(this::toModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return mongo.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return mongo.existsByUsername(username);
    }

    private UserDocument toDocument(User u) {
        UserDocument d = new UserDocument();
        d.setId(u.getId());
        d.setUsername(u.getUsername());
        d.setEmail(u.getEmail());
        d.setPasswordHash(u.getPasswordHash());
        d.setMfaEnabled(u.isMfaEnabled());
        d.setMfaSecret(u.getMfaSecret());
        d.setRole(u.getRole());
        d.setStatus(u.getStatus());
        d.setCreatedAt(u.getCreatedAt());
        d.setUpdatedAt(u.getUpdatedAt());
        return d;
    }

    private User toModel(UserDocument d) {
        return new User(d.getId(), d.getUsername(), d.getEmail(), d.getPasswordHash(),
                        d.isMfaEnabled(), d.getMfaSecret(), d.getRole(),
                        d.getStatus(), d.getCreatedAt(), d.getUpdatedAt());
    }
}