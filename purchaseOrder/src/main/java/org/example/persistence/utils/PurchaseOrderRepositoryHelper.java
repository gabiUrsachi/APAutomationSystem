package org.example.persistence.utils;

import org.bson.Document;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.*;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.*;


public class PurchaseOrderRepositoryHelper {
    public static Criteria createQueryCriteria(List<PurchaseOrderFilter> filters) {
        List<Criteria> criteriaList = new ArrayList<>(filters.size());

        for (PurchaseOrderFilter filter : filters) {
            Criteria criteria = Criteria.where(filter.getCompanyType().toString().toLowerCase()).is(filter.getCompanyUUID());

            if (filter.getRequiredStatus() != null) {
                criteria = criteria.and("orderStatus").is(filter.getRequiredStatus());
            }

            criteriaList.add(criteria);
        }

        return new Criteria().orOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
    }

    public static List<AggregationOperation> createHistoryBasedAggregators(List<PurchaseOrderFilter> filters) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        MatchOperation statusExistsOperation = Aggregation.match(Criteria.where("statusHistory").exists(true));
        String sortString = "{$sortArray: { input: '$statusHistory', sortBy: {date: -1}}}";
        SetOperation setOperation = SetOperation.builder().set("statusHistory").toValue(Document.parse(sortString));
        Criteria matchCriteria = createCriteriaByFilters(filters);
        MatchOperation statusAndCompanyMatchOperation = Aggregation.match(matchCriteria);

        aggregationOperations.add(setOperation);
        aggregationOperations.add(statusAndCompanyMatchOperation);
        aggregationOperations.add(statusExistsOperation);

        return aggregationOperations;
    }

    public static Aggregation createDateBasedAggregation(UUID buyerId, Date lowerTimestamp, Date upperTimestamp) {

        MatchOperation matchStage = Aggregation.match(new Criteria("buyer").is(buyerId));

        AggregationExpression dateGt = ComparisonOperators.Gt.valueOf("status.date")
                .greaterThanValue(lowerTimestamp);

        AggregationExpression dateLt = ComparisonOperators.Lt.valueOf("status.date")
                .lessThanValue(upperTimestamp);

        AggregationExpression dateFilterExpression = BooleanOperators.And.and(dateGt,dateLt);

        AddFieldsOperation addFieldsStage = Aggregation.addFields().addFieldWithValue("statusHistory",
                Filter.filter("statusHistory")
                        .as("status")
                        .by(dateFilterExpression)).build();

        MatchOperation matchStage2 = Aggregation.match(new Criteria("statusHistory.0").exists(true));

        return Aggregation.newAggregation(matchStage, addFieldsStage, matchStage2);
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

    private static Criteria createCriteriaByFilters(List<PurchaseOrderFilter> filters) {
        List<Criteria> criteriaList = new ArrayList<>(filters.size());

        for (PurchaseOrderFilter filter : filters) {
            Criteria criteria = createCompanyCriteria(filter);

            if (filter.getRequiredStatus() != null) {
                Criteria statusCriteria = Criteria.where("statusHistory.0.status").is(filter.getRequiredStatus().toString());
                criteria = new Criteria().andOperator(criteria, statusCriteria);
            }

            criteriaList.add(criteria);
        }

        return new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
    }

    private static Criteria createCompanyCriteria(PurchaseOrderFilter purchaseOrderFilter) {
        String checkedFieldName = purchaseOrderFilter.getCompanyType().toString().toLowerCase();
        UUID checkedFieldValue = purchaseOrderFilter.getCompanyUUID();

        return Criteria.where(checkedFieldName).is(checkedFieldValue);
    }
}
