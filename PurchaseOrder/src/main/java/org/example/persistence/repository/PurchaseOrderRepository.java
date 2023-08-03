package org.example.persistence.repository;

import org.example.persistence.collections.PurchaseOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;
import java.util.UUID;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, UUID> {
    Set<PurchaseOrder> findAllByBuyer(UUID buyer);
}
