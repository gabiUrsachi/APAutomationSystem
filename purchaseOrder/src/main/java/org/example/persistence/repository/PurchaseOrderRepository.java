package org.example.persistence.repository;

import org.example.persistence.collections.PurchaseOrder;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, UUID>, PurchaseOrderCustomRepository {
    @DeleteQuery("{ 'identifier' : ?0 }")
    int customDeleteById(UUID uuid);
}
