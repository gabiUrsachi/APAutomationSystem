package org.example.persistence.repository;

import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.PurchaseOrderHelper;
import org.example.persistence.utils.data.OrderStatus;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PurchaseOrderCustomRepositoryImpl implements PurchaseOrderCustomRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<PurchaseOrder> findByFilters(List<PurchaseOrderFilter> filters) {
        Criteria criteria = PurchaseOrderHelper.createQueryCriteria(filters);
        Query searchQuery = new Query(criteria);

        return this.findAllByQuery(searchQuery);
    }

    @Override
    public PurchaseOrder findByUUIDAndFilters(UUID identifier, List<PurchaseOrderFilter> filters) {
        Criteria criteria = PurchaseOrderHelper.createQueryCriteria(filters);
        criteria = criteria.and("identifier").is(identifier);

        Query searchQuery = new Query(criteria);

        return this.findOneByQuery(searchQuery);
    }

//    @Override
//    public int updateByIdentifierAndVersionAndStatus(UUID identifier, Integer version, OrderStatus orderStatus, PurchaseOrder purchaseOrder) {
//        Query query = new Query(Criteria.where("identifier").is(identifier)
//                .and("version").is(version)
//                .and("orderStatus").is(orderStatus));
//
//        Update update = new Update()
//                .set("buyer", purchaseOrder.getBuyer())
//                .set("seller", purchaseOrder.getSeller())
//                .set("version", purchaseOrder.getVersion())
//                .set("items", purchaseOrder.getItems())
//                .set("orderStatus", purchaseOrder.getOrderStatus());
//
//        return (int) mongoTemplate.updateMulti(query, update, PurchaseOrder.class).getModifiedCount();
//    }


    private List<PurchaseOrder> findAllByQuery(Query query) {
        return mongoTemplate.find(query, PurchaseOrder.class);
    }

    private PurchaseOrder findOneByQuery(Query query) {
        return mongoTemplate.findOne(query, PurchaseOrder.class);
    }

}
