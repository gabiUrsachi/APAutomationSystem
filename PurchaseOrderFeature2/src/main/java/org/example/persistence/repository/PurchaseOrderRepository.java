package org.system.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.system.persistence.collections.PurchaseOrder;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, UUID> {
    Optional<PurchaseOrder> findByIdentifier(UUID Identifier);


}
