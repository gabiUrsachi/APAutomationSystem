package org.example.persistence.repository;

import org.example.persistence.collections.PurchaseOrder;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.UUID;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, UUID>, PurchaseOrderCustomRepository {
    @DeleteQuery("{ 'identifier' : ?0 }")
    int customDeleteById(UUID uuid);

    @Query("{ 'identifier' : ?0, 'version' : ?1 , 'orderStatus' : 'CREATED'}")
    @Update("{ '$set' : { 'buyer' : ?#{#purchaseOrder.buyer}, 'seller' : ?#{#purchaseOrder.seller}, 'version' : ?#{#purchaseOrder.version}, 'items' : ?#{#purchaseOrder.items}, 'orderStatus' : ?#{#purchaseOrder.orderStatus} }}")
    int updateByIdentifierAndVersion(UUID identifier, Integer version, PurchaseOrder purchaseOrder);
}
