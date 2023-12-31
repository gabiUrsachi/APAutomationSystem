package org.example.unit.purchaseOrder.business.services;

import org.example.business.services.PurchaseOrderFilteringService;
import org.example.persistence.utils.CompanyRole;
import org.example.persistence.utils.data.OrderStatus;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.example.utils.data.Roles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseOrderFilteringServiceShould {
    PurchaseOrderFilteringService purchaseOrderFilteringService;

    @Before
    public void setUp() {
        purchaseOrderFilteringService = new PurchaseOrderFilteringService();
    }

    @Test
    public void createAppropriateQueryFiltersByValidRoles() {
        // given
        UUID companyUUID = UUID.randomUUID();
        Set<Roles> userRoles = Set.of(Roles.BUYER_CUSTOMER, Roles.SUPPLIER_ACCOUNTING);

        // when
        List<PurchaseOrderFilter> filters = purchaseOrderFilteringService.createQueryFilters(userRoles, companyUUID);

        // then
        assertEquals(userRoles.size(), filters.size());

        PurchaseOrderFilter buyer1Filter = filters.stream()
                .filter(filter -> filter.getCompanyType() == CompanyRole.BUYER)
                .findFirst()
                .orElse(null);
        assertNotNull(buyer1Filter);
        assertEquals(companyUUID, buyer1Filter.getCompanyUUID());
        assertNull(buyer1Filter.getRequiredStatus());

        PurchaseOrderFilter supplier1Filter = filters.stream()
                .filter(filter -> filter.getCompanyType() == CompanyRole.SELLER)
                .findFirst()
                .orElse(null);
        assertNotNull(supplier1Filter);
        assertEquals(companyUUID, supplier1Filter.getCompanyUUID());
        assertEquals(OrderStatus.APPROVED, supplier1Filter.getRequiredStatus());
    }

    @Test
    public void returnEmptyQueryFilterListForBuyerIIRole() {
        // given
        UUID companyUUID = UUID.randomUUID();
        Set<Roles> userRoles = Set.of(Roles.BUYER_FINANCE);

        // when
        List<PurchaseOrderFilter> filters = purchaseOrderFilteringService.createQueryFilters(userRoles, companyUUID);

        // then
        assertEquals(0, filters.size());
    }
}