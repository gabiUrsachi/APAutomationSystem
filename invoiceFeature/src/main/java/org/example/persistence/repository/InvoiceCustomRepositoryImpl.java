package org.example.persistence.repository;

import org.example.persistence.collections.Invoice;
import org.example.persistence.utils.InvoiceHelper;
import org.example.persistence.utils.data.InvoiceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class InvoiceCustomRepositoryImpl implements InvoiceCustomRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Invoice> findByFilters(List<InvoiceFilter> filters) {
        Criteria criteria = InvoiceHelper.createQueryCriteria(filters);
        Query searchQuery = new Query(criteria);

        return this.findAllByQuery(searchQuery);
    }

    @Override
    public Invoice findByUUIDAndFilters(UUID identifier, List<InvoiceFilter> filters) {
        Criteria criteria = InvoiceHelper.createQueryCriteria(filters);
        criteria = criteria.and("identifier").is(identifier);

        Query searchQuery = new Query(criteria);

        return this.findOneByQuery(searchQuery);
    }


    private List<Invoice> findAllByQuery(Query query) {
        return mongoTemplate.find(query,Invoice.class);
    }

    private Invoice findOneByQuery(Query query) {
        return mongoTemplate.findOne(query,Invoice.class);
    }

}
