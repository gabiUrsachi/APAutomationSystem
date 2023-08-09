package org.example.persistence.repository;

import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.OrderStatus;
import org.springframework.data.mongodb.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, UUID> {
    @DeleteQuery("{ 'identifier' : ?0 }")
    int customDeleteById(UUID uuid);

    @Query("{ 'identifier' : ?0, 'version' : ?1 , 'orderStatus' : ?2}")
    @Update("{ '$set' : { 'buyer' : ?#{#purchaseOrder.buyer}, 'seller' : ?#{#purchaseOrder.seller}, 'version' : ?#{#purchaseOrder.version}, 'items' : ?#{#purchaseOrder.items} }}")
    int updateByIdentifierAndVersionAndOrderStatus(UUID identifier, Integer version, OrderStatus orderStatus, PurchaseOrder purchaseOrder);

//    @Query("{ 'identifier' : ?0, 'version' : ?1 }")
//    @Update("{ '$replaceWith:' : ?2 }")
//    @Aggregation("[{'match: ': {'identifier': ?0, 'version' : ?1}}, { 'replaceWith:' : ?2 }]")
//    PurchaseOrder updateByIdentifierAndVersion(UUID identifier, Integer version, PurchaseOrder purchaseOrder);

//    @Aggregation("[{'match: ': {'version' : ?0}}]")
//    @Aggregation(pipeline = {" { $match: {'version' : ?0}}"})
//    PurchaseOrder getPurchaseOrderByVersion(Integer version);

    //@Update("{ 'identifier' : ?0 , 'version': ?1}")
    //@Update(value = "{'identifier' : ?0 , 'version': ?1}", update = "{'buyer': 1,'seller': 1,'items': 1}")
//    @Update(pipeline = "{$match: {'identifier': ?0, 'version': ?1}, $set: {'buyer': ?2}}")
//    int updateByIdentifierAndVersion(UUID identifier, Integer version,String buyer);
}
