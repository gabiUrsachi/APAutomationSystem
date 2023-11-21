package org.example.persistence.repository;

import org.example.persistence.collections.Invoice;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends MongoRepository<Invoice, String>, InvoiceCustomRepository {

    Optional<Invoice> findByIdentifier(UUID identifier);

    Optional<Invoice> findFirstByBuyerIdOrSellerId(UUID buyerId, UUID sellerId);

    @DeleteQuery("{ 'identifier' : ?0 }")
    int deleteByIdentifier(UUID identifier);

}
