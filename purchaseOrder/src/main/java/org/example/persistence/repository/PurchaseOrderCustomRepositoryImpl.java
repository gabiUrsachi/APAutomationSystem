package org.example.persistence.repository;

import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.PurchaseOrderRepositoryHelper;
import org.example.persistence.utils.data.CompanyOrderStatusChangeMap;
import org.example.persistence.utils.data.PagedPurchaseOrders;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.example.business.utils.PurchaseOrderHistoryHelper.getLatestOrderHistoryObject;

@Component
public class PurchaseOrderCustomRepositoryImpl implements PurchaseOrderCustomRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<PurchaseOrder> findByFilters(List<PurchaseOrderFilter> filters) {
        List<AggregationOperation> aggregationOperations = PurchaseOrderRepositoryHelper.createHistoryBasedAggregators(filters);
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        return this.findAllByAggregation(aggregation);

    }

    @Override
    public Page<PurchaseOrder> findByFiltersPageable(List<PurchaseOrderFilter> filters, Pageable pageable) {
        List<AggregationOperation> aggregationOperations = PurchaseOrderRepositoryHelper.createHistoryBasedAggregators(filters);
        List<AggregationOperation> pagingAggregationOperations = PurchaseOrderRepositoryHelper.createPagingAggregators(pageable);

        aggregationOperations.addAll(pagingAggregationOperations);

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        return this.findAllPagedByAggregation(aggregation);
    }

    @Override
    public PurchaseOrder findByUUIDAndFilters(UUID identifier, List<PurchaseOrderFilter> filters) {
        List<AggregationOperation> aggregationOperations = PurchaseOrderRepositoryHelper.createHistoryBasedAggregators(filters);
        aggregationOperations.add(0, Aggregation.match(new Criteria().and("_id").is(identifier)));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        return this.findOneByAggregation(aggregation);
    }

    @Override
    public List<PurchaseOrder> findByBuyerUUIDAndDate(UUID buyerId, Date lowerTimestamp, Date upperTimestamp) {
        Aggregation aggregation = PurchaseOrderRepositoryHelper.createDateBasedAggregation(buyerId, lowerTimestamp, upperTimestamp);
        return this.findAllByAggregation(aggregation);
    }

    @Override
    public int updateByIdentifierAndVersion(UUID identifier, Integer version, PurchaseOrder purchaseOrder) {
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

        return new PageImpl<>(purchaseOrders, Pageable.unpaged(), pagedPurchaseOrders.getTotal() != null ? pagedPurchaseOrders.getTotal() : 0);
    }

    private PurchaseOrder findOneByAggregation(Aggregation aggregation) {
        AggregationResults<PurchaseOrder> aggregationResults = this.mongoTemplate.aggregate(aggregation, "purchaseOrder", PurchaseOrder.class);
        if (!aggregationResults.getMappedResults().isEmpty()) {
            return aggregationResults.getMappedResults().get(0);
        }

        return null;
    }

    public List<CompanyOrderStatusChangeMap> findStatusCountMapByDate(Date lowerTimestamp, Date upperTimestamp) {

        List<AggregationOperation> aggregationOperations = PurchaseOrderRepositoryHelper.createStatusCountsDateBasedAggregation(lowerTimestamp, upperTimestamp);
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        return this.mongoTemplate.aggregate(aggregation, "purchaseOrder", CompanyOrderStatusChangeMap.class).getMappedResults();

    }

}
