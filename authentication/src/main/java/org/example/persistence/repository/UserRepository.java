package org.example.persistence.repository;

import org.example.persistence.collections.User;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends MongoRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    @DeleteQuery("{ 'identifier' : ?0 }")
    int customDeleteById(UUID uuid);

    Optional<User> findFirstByCompanyIdentifier(UUID companyIdentifier);
}
