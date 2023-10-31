package org.example.persistence.utils;

import org.bson.Document;
import org.example.persistence.utils.data.InvoiceFilter;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InvoiceHelper {
    public static Criteria createQueryCriteria(List<InvoiceFilter> filters) {
        List<Criteria> criteriaList = new ArrayList<Criteria>(filters.size());

        for (InvoiceFilter filter : filters) {
            Criteria criteria = Criteria.where(filter.getCompanyType().toString().toLowerCase()+"Id").is(filter.getCompanyUUID());

            if (filter.getRequiredStatus() != null) {
                criteria = criteria.and("invoiceStatus").is(filter.getRequiredStatus());
            }

            criteriaList.add(criteria);
        }

        return new Criteria().orOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
    }

    public static List<AggregationOperation> createHistoryBasedAggregators(List<InvoiceFilter> filters) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        Optional<InvoiceFilter> anyStatusFilter = filters.stream()
                .filter(invoiceFilter -> invoiceFilter.getRequiredStatus() == null)
                .findFirst();

        if (anyStatusFilter.isPresent()) {
            Criteria companyMatchCriteria = createCompanyCriteria(anyStatusFilter.get());
            MatchOperation companyMatchOperation = Aggregation.match(companyMatchCriteria);

            aggregationOperations.add(companyMatchOperation);
        } else {
            MatchOperation statusExistsOperation = Aggregation.match(Criteria.where("statusHistory").exists(true));
            String sortString = "{$sortArray: { input: '$statusHistory', sortBy: {date: -1}}}";
            SetOperation setOperation = SetOperation.builder().set("statusHistory").toValue(Document.parse(sortString));
            Criteria matchCriteria = createCriteriaByFilters(filters);
            MatchOperation statusAndCompanyMatchOperation = Aggregation.match(matchCriteria);

            aggregationOperations.add(statusExistsOperation);
            aggregationOperations.add(setOperation);
            aggregationOperations.add(statusAndCompanyMatchOperation);
        }

        return aggregationOperations;
    }

    private static Criteria createCriteriaByFilters(List<InvoiceFilter> filters) {
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

    private static Criteria createCompanyCriteria(InvoiceFilter invoiceFilter){
        String checkedFieldName = invoiceFilter.getCompanyType().toString().toLowerCase() + "Id";
        UUID checkedFieldValue = invoiceFilter.getCompanyUUID();

        return Criteria.where(checkedFieldName).is(checkedFieldValue);
    }

    //    public static Aggregation createAggregationOld(List<InvoiceFilter> filters) {
//        List<Aggregation> aggregationList = new ArrayList<>();
//
//        for (InvoiceFilter filter : filters) {
//            MatchOperation companyMatchOperation = Aggregation.match(Criteria.where(filter.getCompanyType().toString().toLowerCase() + "Id").is(filter.getCompanyUUID()));
//            Aggregation aggregation;
//
//            if (filter.getRequiredStatus() != null) {
//
//                MatchOperation statusExists = Aggregation.match(Criteria.where("statusHistory").exists(true));
//                UnwindOperation unwindOperation = Aggregation.unwind("statusHistory");
//                SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("statusHistory.date")));
//                GroupOperation groupOperation = Aggregation.group("identifier").last("$$ROOT").as("doc");
//                ReplaceRootOperation replaceRootOperation = Aggregation.replaceRoot().withValueOf("doc");
//                MatchOperation statusMatch = Aggregation.match(Criteria.where("statusHistory.invoiceStatus").is(filter.getRequiredStatus().toString()));
//
//                aggregation = Aggregation.newAggregation(
//                        companyMatchOperation,
//                        statusExists,
//                        unwindOperation,
//                        sortOperation,
//                        groupOperation,
//                        replaceRootOperation,
//                        statusMatch
//                );
//            } else {
//                aggregation = Aggregation.newAggregation(companyMatchOperation);
//            }
//            aggregationList.add(aggregation);
//        }
//
//
//        return aggregationList.get(0);
//    }
}