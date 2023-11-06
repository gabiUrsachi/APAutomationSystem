package org.example.persistence.utils;

import org.bson.Document;
import org.example.persistence.utils.data.InvoiceFilter;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SetOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class contain methods for creating mongo query criteria and aggregations operations based on given conditions
 */
public class InvoiceHelper {
    public static Criteria createQueryCriteria(List<InvoiceFilter> filters) {
        List<Criteria> criteriaList = new ArrayList<Criteria>(filters.size());

        for (InvoiceFilter filter : filters) {
            Criteria criteria = Criteria.where(filter.getCompanyType().toString().toLowerCase() + "Id").is(filter.getCompanyUUID());

            if (filter.getRequiredStatus() != null) {
                criteria = criteria.and("invoiceStatus").is(filter.getRequiredStatus());
            }

            criteriaList.add(criteria);
        }

        return new Criteria().orOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
    }

    /**
     * This method creates aggregation operations for retrieve and check that the most recent status of an invoice
     * matches the required status from filters for a specific company
     *
     * @param filters conditions used for creating match criteria
     * @return aggregation operations to be applied
     */
    public static List<AggregationOperation> createFiltersBasedAggregators(List<InvoiceFilter> filters) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        MatchOperation statusExistsOperation = Aggregation.match(Criteria.where("statusHistory").exists(true));
        String sortString = "{$sortArray: { input: '$statusHistory', sortBy: {date: -1}}}";
        SetOperation setOperation = SetOperation.builder().set("statusHistory").toValue(Document.parse(sortString));
        Criteria statusAndCompanyCriteria = createStatusAndCompanyCriteria(filters);
        MatchOperation statusAndCompanyMatchOperation = Aggregation.match(statusAndCompanyCriteria);

        aggregationOperations.add(statusExistsOperation);
        aggregationOperations.add(setOperation);
        aggregationOperations.add(statusAndCompanyMatchOperation);

        return aggregationOperations;
    }

    /**
     * This method creates aggregation operations for retrieve the total paid amount over some
     * past months for a specific customer/buyer
     *
     * @param buyerId the buyer whose total paid amount will be retrieved
     * @param monthsNumber the number of considered past months
     * @return aggregation operations to be applied
     */
    public static List<AggregationOperation> createPaidAmountOverNMonthsAggregators(UUID buyerId, int monthsNumber) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("buyerId").is(buyerId));
        criteriaList.add(Criteria.where("statusHistory").exists(true));
        Criteria buyerAndHistoryMatchCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("statusHistory.invoiceStatus").is(InvoiceStatus.PAID.toString()));
        criteriaList.add(Criteria.where("statusHistory.date").gte(LocalDateTime.now().minusMonths(monthsNumber)));
        Criteria invoiceStatusMatchCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        aggregationOperations.add(Aggregation.match(buyerAndHistoryMatchCriteria));
        aggregationOperations.add(Aggregation.unwind("statusHistory"));
        aggregationOperations.add(Aggregation.match(invoiceStatusMatchCriteria));
        aggregationOperations.add(Aggregation.group("buyerId").sum("totalAmount").as("totalAmount"));
        aggregationOperations.add(Aggregation.project("totalAmount"));
        aggregationOperations.add(Aggregation.project().andExclude("_id"));

        return aggregationOperations;
    }

    private static Criteria createStatusAndCompanyCriteria(List<InvoiceFilter> filters) {
        List<Criteria> criteriaList = new ArrayList<>(filters.size());

        for (InvoiceFilter filter : filters) {
            Criteria criteria = createCompanyCriteria(filter);

            if (filter.getRequiredStatus() != null) {
                Criteria statusCriteria = Criteria.where("statusHistory.0.invoiceStatus").is(filter.getRequiredStatus().toString());
                criteria = new Criteria().andOperator(criteria, statusCriteria);
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
}