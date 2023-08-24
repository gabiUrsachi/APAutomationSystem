package org.example.business.services;

import org.example.persistence.utils.CompanyRole;
import org.example.persistence.utils.InvoiceStatus;
import org.example.persistence.utils.data.InvoiceFilter;
import org.example.utils.data.Roles;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class InvoiceFilteringService {

    public List<InvoiceFilter> createQueryFilters(Set<Roles> userRoles, UUID companyUUID) {
        List<InvoiceFilter> filters = new ArrayList<>(userRoles.size());

        for (Roles role : userRoles) {
            InvoiceFilter newFilter;

            switch (role) {
                case BUYER_I:
                    break;
                case BUYER_II:
                    newFilter = InvoiceFilter.builder()
                            .companyUUID(companyUUID)
                            .companyType(CompanyRole.BUYER)
                            .requiredStatus(InvoiceStatus.APPROVED)
                            .build();
                    filters.add(newFilter);

                    newFilter = InvoiceFilter.builder()
                            .companyUUID(companyUUID)
                            .companyType(CompanyRole.BUYER)
                            .requiredStatus(InvoiceStatus.PAID)
                            .build();

                    filters.add(newFilter);
                    break;
                case SUPPLIER_I:
                    newFilter = InvoiceFilter.builder()
                            .companyUUID(companyUUID)
                            .companyType(CompanyRole.SELLER)
                            .build();
                    filters.add(newFilter);
                    break;
                case SUPPLIER_II:
                    break;
            }
        }
        return filters;
    }
}