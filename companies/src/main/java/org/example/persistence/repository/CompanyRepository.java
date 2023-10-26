package org.example.persistence.repository;

import org.example.persistence.collections.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CompanyRepository extends MongoRepository<Company, UUID>, CompanyCustomRepository {
    Optional<Company> findById(UUID uuid);
    Company findByName(String name);
}
