package org.example.persistence.repository;

import org.example.persistence.collections.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.example.persistence.collections.PurchaseOrder;

import java.util.Set;
import java.util.UUID;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, UUID> {
//    Optional<PurchaseOrder> findBy

    Set<PurchaseOrder> findAllByBuyer(Company buyer);

    //PurchaseOrder updateByIdentifier(UUID uuid, PurchaseOrder purchaseOrder);
}
