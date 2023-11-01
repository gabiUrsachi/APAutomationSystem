package org.example.business.services;

import org.example.persistence.utils.CompanyRole;
import org.example.utils.data.Roles;
import org.example.persistence.utils.data.OrderStatus;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * This service provides methods to create filters for purchase orders based on user roles and company UUID.
 */
@Service
public class PurchaseOrderFilteringService {

    /**
     * Creates and returns a list of purchase order query filters based on user roles and user's company UUID.
     *
     * @param userRoles   the set of roles associated with the user
     * @param companyUUID the UUID of the user's company
     * @return a list of PurchaseOrderFilter objects representing filters for purchase orders
     */
    public List<PurchaseOrderFilter> createQueryFilters(Set<Roles> userRoles, UUID companyUUID) {
        List<PurchaseOrderFilter> filters = new ArrayList<>(userRoles.size());

        for (Roles role : userRoles) {
            PurchaseOrderFilter newFilter;

            switch (role) {
                case BUYER_CUSTOMER:
                    newFilter = PurchaseOrderFilter.builder()
                            .companyUUID(companyUUID)
                            .companyType(CompanyRole.BUYER)
                            .build();

                    filters.add(newFilter);
                    break;
                case BUYER_FINANCE:
                    break;
                case SUPPLIER_ACCOUNTING:
                    newFilter = PurchaseOrderFilter.builder()
                            .companyUUID(companyUUID)
                            .companyType(CompanyRole.SELLER)
//                            .requiredStatus(OrderStatus.APPROVED)
                            .build();
                    filters.add(newFilter);
                    break;
                case SUPPLIER_MANAGEMENT:
                    newFilter = PurchaseOrderFilter.builder()
                            .companyUUID(companyUUID)
                            .companyType(CompanyRole.SELLER)
//                            .requiredStatus(OrderStatus.SAVED)
                            .build();
                    filters.add(newFilter);
                    break;
            }
        }
        return filters;
    }
}
