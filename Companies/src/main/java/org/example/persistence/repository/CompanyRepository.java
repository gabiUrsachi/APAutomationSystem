package org.example.persistence.repository;

import org.example.persistence.collections.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;
import java.util.UUID;

public interface CompanyRepository extends MongoRepository<Company, UUID> {
    Set<Company> findAllByName(String name);
    Company findByName(String name);
}
