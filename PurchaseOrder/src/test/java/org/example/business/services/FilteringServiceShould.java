package org.example.business.services;

import org.example.persistence.utils.CompanyRole;
import org.example.persistence.utils.data.OrderStatus;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.example.utils.Roles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class FilteringServiceShould {
    FilteringService filteringService;

    @Before
    public void setUp() {
        filteringService = new FilteringService();
    }

    @Test
    public void createAppropriateQueryFiltersByValidRoles() {
        // given
        UUID companyUUID = UUID.randomUUID();
        Set<Roles> userRoles = Set.of(Roles.BUYER_I, Roles.SUPPLIER_I);

        // when
        List<PurchaseOrderFilter> filters = filteringService.createQueryFilters(userRoles, companyUUID);

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
        Set<Roles> userRoles = Set.of(Roles.BUYER_II);

        // when
        List<PurchaseOrderFilter> filters = filteringService.createQueryFilters(userRoles, companyUUID);

        // then
        assertEquals(0, filters.size());
    }
}