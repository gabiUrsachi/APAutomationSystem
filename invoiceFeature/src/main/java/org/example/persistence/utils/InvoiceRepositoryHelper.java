package org.example.persistence.utils;

import com.mongodb.BasicDBObject;
import org.example.business.utils.CompanyInvoiceStatusTaxMap;
import org.example.business.utils.InvoiceStatusTaxPair;
import org.example.business.utils.InvoiceTaxationRate;
import org.example.persistence.utils.data.CompanyInvoiceStatusChangeMap;
import org.example.persistence.utils.data.InvoiceFilter;
import org.example.persistence.utils.data.InvoiceOccurrencePair;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * This class contain methods for creating mongo query criteria and aggregations operations based on given conditions
 */
public class InvoiceRepositoryHelper {
    public static Criteria createQueryCriteria(List<InvoiceFilter> filters) {
        List<Criteria> criteriaList = new ArrayList<Criteria>(filters.size());

        for (InvoiceFilter filter : filters) {
            Criteria criteria = Criteria.where(filter.getCompanyType().toString().toLowerCase() + "Id").is(filter.getCompanyUUID());

            if (filter.getRequiredStatus() != null) {
                criteria = criteria.and("status").is(filter.getRequiredStatus());
            }

            criteriaList.add(criteria);
        }

        return new Criteria().orOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
    }

    /**
     * This method creates a list of criteria intended to match invoices from a buyer to a supplier
     * that have the change date of the required status not before the given number of months from now
     *
     * @param buyerUUID     the buyer whose invoices are checked
     * @param sellerUUID    the supplier of teh checked invoices
     * @param invoiceStatus searched invoice's status
     * @param pastMonths    valid past months from now
     * @return all created criteria according to the given requirements
     */
    public static List<Criteria> createStatusAndDiscountBasedCriteria(UUID buyerUUID, UUID sellerUUID, InvoiceStatus invoiceStatus, int pastMonths) {
        Criteria buyerCriteria = Criteria.where("buyerId").is(buyerUUID);
        Criteria sellerCriteria = Criteria.where("sellerId").is(sellerUUID);

        Criteria discountCriteria = new Criteria().andOperator(
                Criteria.where("discountRate").exists(true),
                Criteria.where("discountRate").ne(null)
        );

        Criteria statusCriteria = Criteria.where("statusHistory").elemMatch(
                Criteria
                        .where("status").is(invoiceStatus.toString())
                        .and("date").gte(LocalDateTime.now().minusMonths(pastMonths))
        );

        return List.of(buyerCriteria, sellerCriteria, discountCriteria, statusCriteria);
    }

    /**
     * This method creates aggregation operations for retrieve and check that the status of an invoice
     * matches the required status from filters for a specific company
     *
     * @param filters conditions used for creating match criteria
     * @return aggregation operations to be applied
     */
    public static List<AggregationOperation> createFiltersBasedAggregators(List<InvoiceFilter> filters) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        Criteria statusAndCompanyCriteria = createStatusAndCompanyCriteria(filters);
        aggregationOperations.add(Aggregation.match(statusAndCompanyCriteria));

        return aggregationOperations;
    }

    /**
     * This method creates aggregation operations for retrieve the total paid amount over some
     * past months for a specific customer/buyer
     *
     * @param buyerId      the buyer whose total paid amount will be retrieved
     * @param monthsNumber the number of considered past months
     * @return aggregation operations to be applied
     */
    public static List<AggregationOperation> createPaidAmountOverNMonthsAggregators(UUID buyerId, UUID sellerId, int monthsNumber) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        Criteria buyerCriteria = Criteria.where("buyerId").is(buyerId);
        Criteria supplierCriteria = Criteria.where("sellerId").is(sellerId);
        Criteria companiesCriteria = new Criteria().andOperator(buyerCriteria, supplierCriteria);

        Criteria statusCriteria = Criteria.where("status").is(InvoiceStatus.PAID.toString());
        Criteria dateCriteria = Criteria.where("date").gte(LocalDateTime.now().minusMonths(monthsNumber));
        Criteria statusHistoryCriteria = Criteria.where("statusHistory").elemMatch(new Criteria().andOperator(statusCriteria, dateCriteria));

        Criteria matchCriteria = new Criteria().andOperator(companiesCriteria, statusHistoryCriteria);

        aggregationOperations.add(Aggregation.match(matchCriteria));
        aggregationOperations.add(Aggregation.group("buyerId").sum("totalAmount").as("totalAmount"));
        aggregationOperations.add(Aggregation.project("totalAmount").andExclude("_id"));

        return aggregationOperations;
    }

    /**
     * This method creates aggregation operators for retrieving both paginated content of a collection
     * and the total number of its documents
     *
     * @param pageable pagination params
     * @return a list of aggregators based on the given params for pagination
     */
    public static List<AggregationOperation> createPagingAggregators(Pageable pageable) {
        FacetOperation facetOperation = new FacetOperation();

        facetOperation = facetOperation.and(
                        new SkipOperation((long) pageable.getPageNumber() * pageable.getPageSize()),
                        Aggregation.limit(pageable.getPageSize()))
                .as("content");

        facetOperation = facetOperation.and(
                Aggregation.group().count().as("total"),
                Aggregation.project("total").andExclude("_id")).as("total");

        ProjectionOperation projectionOperation = Aggregation
                .project("total")
                .and("total.total")
                .arrayElementAt(0)
                .as("total")
                .andInclude("content");

        return List.of(
                facetOperation,
                projectionOperation
        );
    }

    private static Criteria createStatusAndCompanyCriteria(List<InvoiceFilter> filters) {
        List<Criteria> criteriaList = new ArrayList<>(filters.size());

        for (InvoiceFilter filter : filters) {
            Criteria criteria = createCompanyCriteria(filter);

            if (filter.getRequiredStatus() != null) {
                Criteria statusCriteria = Criteria.where("status").is(filter.getRequiredStatus().toString());
                Criteria statusHistoryCriteria = Criteria.where("statusHistory").elemMatch(statusCriteria);

                criteria = new Criteria().andOperator(criteria, statusHistoryCriteria);
            }

            criteriaList.add(criteria);
        }

        return new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
    }

    private static Criteria createCompanyCriteria(InvoiceFilter invoiceFilter) {
        String checkedFieldName = invoiceFilter.getCompanyType().toString().toLowerCase() + "Id";
        UUID checkedFieldValue = invoiceFilter.getCompanyUUID();

        return Criteria.where(checkedFieldName).is(checkedFieldValue);
    }

    public static Aggregation createDateBasedAggregation(UUID sellerId, Date lowerTimestamp, Date upperTimestamp) {

        MatchOperation matchStage = Aggregation.match(new Criteria("sellerId").is(sellerId));

        AggregationExpression dateGt = ComparisonOperators.Gt.valueOf("status.date")
                .greaterThanValue(lowerTimestamp);

        AggregationExpression dateLt = ComparisonOperators.Lt.valueOf("status.date")
                .lessThanValue(upperTimestamp);

        AggregationExpression dateFilterExpression = BooleanOperators.And.and(dateGt,dateLt);

        AddFieldsOperation addFieldsStage = Aggregation.addFields().addFieldWithValue("statusHistory",
                ArrayOperators.Filter.filter("statusHistory")
                        .as("status")
                        .by(dateFilterExpression)).build();

        MatchOperation matchStage2 = Aggregation.match(new Criteria("statusHistory.0").exists(true));

        return Aggregation.newAggregation(matchStage, addFieldsStage, matchStage2);
    }

    public static List<AggregationOperation> createStatusCountsDateBasedAggregation(Date lowerTimestamp, Date upperTimestamp) {

        List<AggregationOperation> aggregationOperationList = new ArrayList<>();
        MatchOperation matchOperation = Aggregation.match(Criteria.where("statusHistory.date").gte(lowerTimestamp).lte(upperTimestamp));
        UnwindOperation unwindOperation = Aggregation.unwind("statusHistory");
        MatchOperation dateCriteria = Aggregation.match(Criteria.where("statusHistory.date").gte(lowerTimestamp).lte(upperTimestamp));
        GroupOperation groupOperation = Aggregation.group(Fields.fields().and("sellerId").and("statusHistory.status")).count().as("count");
        GroupOperation groupByStatusCountsOperation = Aggregation.group("_id.sellerId").push(
                new BasicDBObject("status", "$_id.status").append("count", "$count")
        ).as("statusCounts");

        aggregationOperationList.add(matchOperation);
        aggregationOperationList.add(unwindOperation);
        aggregationOperationList.add(dateCriteria);
        aggregationOperationList.add(groupOperation);
        aggregationOperationList.add(groupByStatusCountsOperation);

        return aggregationOperationList;


    }

    public static List<CompanyInvoiceStatusTaxMap> createCompanyStatusTaxByCounts(List<CompanyInvoiceStatusChangeMap> invoiceCountMapList) {

        List<CompanyInvoiceStatusTaxMap> invoiceCompanyTax = new ArrayList<>();
        for (CompanyInvoiceStatusChangeMap companyStatusMap : invoiceCountMapList) {
            List<InvoiceStatusTaxPair> invoiceStatusTaxPairs = new ArrayList<>();
            for (InvoiceOccurrencePair invoiceOccurrencePair : companyStatusMap.getStatusCounts()) {
                invoiceStatusTaxPairs.add(new InvoiceStatusTaxPair(invoiceOccurrencePair.getStatus(),invoiceOccurrencePair.getCount() * InvoiceTaxationRate.invoiceTaxRate.get(invoiceOccurrencePair.getStatus())));
            }
            CompanyInvoiceStatusTaxMap companyTaxMap = CompanyInvoiceStatusTaxMap.builder()
                    .companyUUID(companyStatusMap.get_id())
                    .invoiceStatusTaxPairs(invoiceStatusTaxPairs)
                    .build();
            invoiceCompanyTax.add(companyTaxMap);
        }

        return invoiceCompanyTax;
    }
}