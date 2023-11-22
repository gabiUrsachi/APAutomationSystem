package org.example.persistence.repository;

import org.example.persistence.collections.Invoice;
import org.example.persistence.utils.InvoiceHelper;
import org.example.persistence.utils.InvoiceStatusHistoryHelper;
import org.example.persistence.utils.data.InvoiceFilter;
import org.example.persistence.utils.data.PagedInvoices;
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

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class InvoiceCustomRepositoryImpl implements InvoiceCustomRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Invoice> findByFilters(List<InvoiceFilter> filters) {
        List<AggregationOperation> aggregationOperations = InvoiceHelper.createFiltersBasedAggregators(filters);
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        return this.findAllByAggregation(aggregation);
    }

    @Override
    public Page<Invoice> findByFiltersPageable(List<InvoiceFilter> filters, Pageable pageable) {
        List<AggregationOperation> aggregationOperations = InvoiceHelper.createFiltersBasedAggregators(filters);
        List<AggregationOperation> pagingAggregationOperations = InvoiceHelper.createPagingAggregators(pageable);

        aggregationOperations.addAll(pagingAggregationOperations);

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        return this.findAllPagedByAggregation(aggregation);
    }

    @Override
    public Invoice findByUUIDAndFilters(UUID identifier, List<InvoiceFilter> filters) {
        List<AggregationOperation> aggregationOperations = InvoiceHelper.createFiltersBasedAggregators(filters);
        aggregationOperations.add(0, Aggregation.match(new Criteria().and("_id").is(identifier)));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        return this.findOneByAggregation(aggregation);
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
                .set("discountRate", invoice.getDiscountRate())
                .addToSet("statusHistory", InvoiceStatusHistoryHelper.getMostRecentHistoryObject(invoice.getStatusHistory()));

        return (int) mongoTemplate.updateMulti(query, update, Invoice.class).getModifiedCount();
    }

    /**
     * This method is used for retrieving the total paid amount over some past months for a specific customer/buyer
     *
     * @param buyerId      the buyer whose total paid amount is retrieved
     * @param monthsNumber the number of considered past months
     * @return the total paid amount
     */
    @Override
    public Float getPaidAmountForLastNMonths(UUID buyerId, int monthsNumber) {
        List<AggregationOperation> aggregationOperations = InvoiceHelper.createPaidAmountOverNMonthsAggregators(buyerId, monthsNumber);
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        Invoice resultedInvoice = this.mongoTemplate.aggregate(aggregation, "invoice", Invoice.class).getUniqueMappedResult();
        return resultedInvoice != null ? resultedInvoice.getTotalAmount() : null;
    }

    @Override
    public List<Invoice> findByBuyerUUIDAndDate(UUID sellerId, Date lowerTimestamp, Date upperTimestamp) {
        Aggregation aggregation = InvoiceHelper.createDateBasedAggregation(sellerId, lowerTimestamp, upperTimestamp);
        return this.findAllByAggregation(aggregation);
    }

    private List<Invoice> findAllByQuery(Query query) {
        return mongoTemplate.find(query, Invoice.class);
    }

    private List<Invoice> findAllByAggregation(Aggregation aggregation) {
        return this.mongoTemplate.aggregate(aggregation, "invoice", Invoice.class).getMappedResults();
    }

    private Page<Invoice> findAllPagedByAggregation(Aggregation aggregation) {
        PagedInvoices pagedInvoices = this.mongoTemplate.aggregate(aggregation, "invoice", PagedInvoices.class).getUniqueMappedResult();
        List<Invoice> invoices = pagedInvoices.getContent();

        return new PageImpl<>(invoices, Pageable.unpaged(), pagedInvoices.getTotal() != null ? pagedInvoices.getTotal() : 0);
    }

    private Invoice findOneByQuery(Query query) {
        return mongoTemplate.findOne(query, Invoice.class);
    }

    private Invoice findOneByAggregation(Aggregation aggregation) {
        AggregationResults<Invoice> aggregationResults = this.mongoTemplate.aggregate(aggregation, "invoice", Invoice.class);
        if (!aggregationResults.getMappedResults().isEmpty()) {
            return aggregationResults.getMappedResults().get(0);
        }

        return null;
    }

}
