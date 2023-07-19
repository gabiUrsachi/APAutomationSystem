package org.system.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.system.persistence.collections.PurchaseOrder;

import java.util.Optional;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, String> {
    Optional<PurchaseOrder> findByIdentifier(String Identifier);
}
