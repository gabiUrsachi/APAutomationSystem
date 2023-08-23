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

    @Query("{ 'identifier' : ?0, 'version' : ?1 , 'invoiceStatus' : 'CREATED'}")
    @Update("{ '$set' : { 'buyerId' : ?#{#invoice.buyerId}, 'sellerId' : ?#{#invoice.sellerId}, 'version' : ?#{#invoice.version}, 'items' : ?#{#invoice.items}, 'invoiceStatus' : ?#{#invoice.invoiceStatus} }}")
    int updateByIdentifierAndVersion(UUID identifier, Integer version, Invoice invoice);
}
