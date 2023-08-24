package org.example.persistence.repository;

import org.example.persistence.collections.Invoice;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.InvoiceHelper;
import org.example.persistence.utils.data.InvoiceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    @Override
    public int updateByIdentifierAndVersion(UUID identifier, Integer version, Invoice invoice) {
        Query query = new Query(Criteria.where("identifier").is(identifier)
                .and("version").is(version));

        Update update = new Update()
                .set("buyerId", invoice.getBuyerId())
                .set("sellerId", invoice.getSellerId())
                .set("version", invoice.getVersion())
                .set("items", invoice.getItems())
                .set("invoiceStatus", invoice.getInvoiceStatus());

        return (int) mongoTemplate.updateMulti(query, update, Invoice.class).getModifiedCount();
    }


    private List<Invoice> findAllByQuery(Query query) {
        return mongoTemplate.find(query, Invoice.class);
    }

    private Invoice findOneByQuery(Query query) {
        return mongoTemplate.findOne(query, Invoice.class);
    }

}
