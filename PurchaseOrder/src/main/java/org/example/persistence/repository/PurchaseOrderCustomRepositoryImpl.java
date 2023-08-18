package org.example.persistence.repository;

import org.example.persistence.collections.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PurchaseOrderCustomRepositoryImpl implements PurchaseOrderCustomRepository{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<PurchaseOrder> findByQuery(Query query, Class<PurchaseOrder> purchaseOrderClass) {
        return mongoTemplate.find(query, PurchaseOrder.class);
    }
}
