package co.casino.auth.infrastructure.adapter.out.persistence.repository;

import co.casino.auth.infrastructure.adapter.out.persistence.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserMongoRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByEmail(String email);
    Optional<UserDocument> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}