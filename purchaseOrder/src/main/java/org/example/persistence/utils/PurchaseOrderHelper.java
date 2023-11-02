package org.example.persistence.utils;

import org.bson.Document;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SetOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PurchaseOrderHelper {
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
