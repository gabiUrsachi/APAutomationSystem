package org.example.unit.purchaseOrder.persistence.utils;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.persistence.utils.CompanyRole;
import org.example.persistence.utils.PurchaseOrderRepositoryHelper;
import org.example.persistence.utils.data.OrderStatus;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseOrderRepositoryHelperShould {

    @Test
    public void createValidQueryCriteriaFromOneFilter() {
        // given
        UUID uuid = UUID.randomUUID();
        PurchaseOrderFilter purchaseOrderFilter = PurchaseOrderFilter.builder()
                .requiredStatus(OrderStatus.CREATED)
                .companyType(CompanyRole.BUYER)
                .companyUUID(uuid)
                .build();

        // when
        Criteria createdCriteria = PurchaseOrderRepositoryHelper.createQueryCriteria(List.of(purchaseOrderFilter));

        // then
        Document criteriaObject = createdCriteria.getCriteriaObject();
        Assertions.assertTrue(criteriaObject.containsKey("$or"));

        Object orClause = criteriaObject.get("$or");
        Assertions.assertTrue(orClause instanceof List);

        List<Bson> orCriteriaList = (List<Bson>) orClause;
        Assertions.assertEquals(1, orCriteriaList.size()); // one filter only

        Bson subCriteria = orCriteriaList.get(0);
        Document subCriteriaDocument = (Document) subCriteria;
        Assertions.assertEquals(uuid, subCriteriaDocument.get(CompanyRole.BUYER.toString().toLowerCase()));
        Assertions.assertEquals(OrderStatus.CREATED, subCriteriaDocument.get("orderStatus"));
    }


    @Test
    public void createValidQueryCriteriaFromMultipleFilters() {
        // Given
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        List<PurchaseOrderFilter> purchaseOrderFilters = List.of(
                PurchaseOrderFilter.builder()
                        .requiredStatus(OrderStatus.CREATED)
                        .companyType(CompanyRole.BUYER)
                        .companyUUID(uuid1)
                        .build(),
                PurchaseOrderFilter.builder()
                        .companyType(CompanyRole.SELLER)
                        .companyUUID(uuid2)
                        .build()
        );

        // When
        Criteria createdCriteria = PurchaseOrderRepositoryHelper.createQueryCriteria(purchaseOrderFilters);

        // Then
        Document criteriaObject = createdCriteria.getCriteriaObject();
        Assertions.assertTrue(criteriaObject.containsKey("$or"));

        Object orClause = criteriaObject.get("$or");
        Assertions.assertTrue(orClause instanceof List);

        List<Bson> orCriteriaList = (List<Bson>) orClause;
        Assertions.assertEquals(2, orCriteriaList.size());

        Bson subCriteria1 = orCriteriaList.get(0);
        Document subCriteriaDocument1 = (Document) subCriteria1;
        Assertions.assertEquals(uuid1, subCriteriaDocument1.get(CompanyRole.BUYER.toString().toLowerCase()));
        Assertions.assertEquals(OrderStatus.CREATED, subCriteriaDocument1.get("orderStatus"));

        Bson subCriteria2 = orCriteriaList.get(1);
        Document subCriteriaDocument2 = (Document) subCriteria2;
        Assertions.assertEquals(uuid2, subCriteriaDocument2.get(CompanyRole.SELLER.toString().toLowerCase()));
    }
}