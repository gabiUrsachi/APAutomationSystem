package org.example.persistence.repository;

import org.example.persistence.collections.Company;
import org.example.persistence.collections.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends MongoRepository<Invoice, String>, InvoiceCustomRepository {

    Optional<Invoice> findByIdentifier(UUID identifier);

    void deleteByIdentifier(UUID identifier);

}
