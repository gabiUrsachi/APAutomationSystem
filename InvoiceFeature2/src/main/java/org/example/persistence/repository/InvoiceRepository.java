package org.example.persistence.repository;

import org.example.persistence.collections.Company;
import org.example.persistence.collections.Invoice;
import org.example.persistence.collections.PurchaseOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    Optional<Invoice> findByIdentifier(UUID identifier);

    void deleteByIdentifier(UUID identifier);
}
