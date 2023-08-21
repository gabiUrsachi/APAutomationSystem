package org.example.persistence.repository;

import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.PurchaseOrderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PurchaseOrderCustomRepositoryImpl implements PurchaseOrderCustomRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<PurchaseOrder> findByFilters(List<PurchaseOrderFilter> filters) {
        Criteria criteria = createFilters(filters);
        Query searchQuery = new Query(criteria);

        return this.findAllByQuery(searchQuery);
    }

    @Override
    public PurchaseOrder findByUUIDAndFilters(UUID identifier, List<PurchaseOrderFilter> filters) {
        Criteria criteria = createFilters(filters);
        criteria = criteria.and("identifier").is(identifier);

        Query searchQuery = new Query(criteria);

        return this.findOneByQuery(searchQuery);
    }

    private Criteria createFilters(List<PurchaseOrderFilter> filters) {
        List<Criteria> criteriaList = new ArrayList<Criteria>(filters.size());

        for (PurchaseOrderFilter filter : filters) {
            Criteria criteria = Criteria.where(filter.getCompanyType()).is(filter.getCompanyUUID());

            if (filter.getOrderStatus() != null) {
                criteria = criteria.and("orderStatus").is(filter.getOrderStatus());
            }

            criteriaList.add(criteria);
        }

        return new Criteria().orOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
    }

    private List<PurchaseOrder> findAllByQuery(Query query) {
        return mongoTemplate.find(query, PurchaseOrder.class);
    }

    private PurchaseOrder findOneByQuery(Query query) {
        return mongoTemplate.findOne(query, PurchaseOrder.class);
    }

}
