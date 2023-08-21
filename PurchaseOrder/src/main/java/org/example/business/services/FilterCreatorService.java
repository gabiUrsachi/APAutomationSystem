package org.example.business.services;

import org.example.Roles;
import org.example.persistence.utils.PurchaseOrderFilter;
import org.example.persistence.utils.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class FilterCreatorService {

    public List<PurchaseOrderFilter> createFilters(Set<Roles> userRoles, UUID companyUUID) {
        List<PurchaseOrderFilter> filters = new ArrayList<>(userRoles.size());

        for (Roles role : userRoles) {
            PurchaseOrderFilter newFilter;

            switch (role){
                case BUYER_I:
                    newFilter = new PurchaseOrderFilter(null, companyUUID, "buyer");
                    filters.add(newFilter);
                    break;
                case BUYER_II:
                    break;
                case SUPPLIER_I:
                    newFilter = new PurchaseOrderFilter(OrderStatus.APPROVED, companyUUID, "seller");
                    filters.add(newFilter);
                    break;
                case SUPPLIER_II:
                    newFilter = new PurchaseOrderFilter(OrderStatus.SAVED, companyUUID, "seller");
                    filters.add(newFilter);
                    break;
            }
        }

        return filters;
    }
}
