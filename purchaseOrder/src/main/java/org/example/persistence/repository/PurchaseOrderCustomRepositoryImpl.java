package org.example.persistence.repository;

import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.PurchaseOrderHelper;
import org.example.persistence.utils.data.OrderStatus;
import org.example.persistence.utils.data.PagedPurchaseOrders;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.example.business.utils.PurchaseOrderHistoryHelper.getLatestOrderHistoryObject;

@Component
public class PurchaseOrderCustomRepositoryImpl implements PurchaseOrderCustomRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<PurchaseOrder> findByFilters(List<PurchaseOrderFilter> filters) {
        List<AggregationOperation> aggregationOperations = PurchaseOrderHelper.createHistoryBasedAggregators(filters);
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        return this.findAllByAggregation(aggregation);

    }

    @Override
    public Page<PurchaseOrder> findByFiltersPageable(List<PurchaseOrderFilter> filters, Pageable pageable) {
        List<AggregationOperation> aggregationOperations = PurchaseOrderHelper.createHistoryBasedAggregators(filters);
        List<AggregationOperation> pagingAggregationOperations = PurchaseOrderHelper.createPagingAggregators(pageable);

        aggregationOperations.addAll(pagingAggregationOperations);

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        return this.findAllPagedByAggregation(aggregation);
    }

    @Override
    public PurchaseOrder findByUUIDAndFilters(UUID identifier, List<PurchaseOrderFilter> filters) {
        List<AggregationOperation> aggregationOperations = PurchaseOrderHelper.createHistoryBasedAggregators(filters);
        aggregationOperations.add(0, Aggregation.match(new Criteria().and("_id").is(identifier)));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        return this.findOneByAggregation(aggregation);
    }

    @Override
    public List<PurchaseOrder> findByBuyerUUIDAndDate(UUID buyerId, Date lowerTimestamp, Date upperTimestamp) {
        Aggregation aggregation = PurchaseOrderHelper.createDateBasedAggregation(buyerId, lowerTimestamp, upperTimestamp);
        return this.findAllByAggregation(aggregation);
    }

    @Override
    public int updateByIdentifierAndVersion(UUID identifier, Integer version, OrderStatus orderStatus, PurchaseOrder purchaseOrder) {
        Query query = new Query(Criteria.where("identifier").is(identifier)
                .and("version").is(version));

        Update update = new Update()
                .set("buyer", purchaseOrder.getBuyer())
                .set("seller", purchaseOrder.getSeller())
                .set("version", purchaseOrder.getVersion())
                .set("items", purchaseOrder.getItems())
                .addToSet("statusHistory", getLatestOrderHistoryObject(purchaseOrder.getStatusHistory()));

        return (int) mongoTemplate.updateMulti(query, update, PurchaseOrder.class).getModifiedCount();
    }


    private List<PurchaseOrder> findAllByAggregation(Aggregation aggregation) {
        return this.mongoTemplate.aggregate(aggregation, "purchaseOrder", PurchaseOrder.class).getMappedResults();
    }

    private Page<PurchaseOrder> findAllPagedByAggregation(Aggregation aggregation) {
        PagedPurchaseOrders pagedPurchaseOrders = this.mongoTemplate.aggregate(aggregation, "purchaseOrder", PagedPurchaseOrders.class).getUniqueMappedResult();
        List<PurchaseOrder> purchaseOrders = pagedPurchaseOrders.getContent();

        return new PageImpl<>(purchaseOrders, Pageable.unpaged(), pagedPurchaseOrders.getTotal());
    }

    private PurchaseOrder findOneByAggregation(Aggregation aggregation) {
        AggregationResults<PurchaseOrder> aggregationResults = this.mongoTemplate.aggregate(aggregation, "purchaseOrder", PurchaseOrder.class);
        if (!aggregationResults.getMappedResults().isEmpty()) {
            return aggregationResults.getMappedResults().get(0);
        }

        return null;
    }

}
