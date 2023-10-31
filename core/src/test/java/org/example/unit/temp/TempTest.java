package org.example.unit.temp;

import org.example.persistence.utils.CompanyRole;
import org.example.persistence.utils.InvoiceStatus;
import org.example.persistence.utils.data.InvoiceFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class TempTest {


    @Test
    public void test1() {
        Date date = new Date();
        System.out.println(date);

        LocalDate localDate = LocalDate.now();
        System.out.println(localDate);
    }

    @Test
    public void test2() {
        List<InvoiceFilter> filters = new ArrayList<>(2);
        filters.add(InvoiceFilter.builder()
                .companyUUID(UUID.randomUUID())
                .companyType(CompanyRole.BUYER)
                .requiredStatus(InvoiceStatus.SENT)
                .build());
        filters.add(InvoiceFilter.builder()
                .companyUUID(UUID.randomUUID())
                .companyType(CompanyRole.BUYER)
                .requiredStatus(InvoiceStatus.PAID)
                .build());

        List<Criteria> criteriaList = new ArrayList<Criteria>(filters.size());

        for (InvoiceFilter filter : filters) {
            Criteria criteria = Criteria.where(filter.getCompanyType().toString().toLowerCase()+"Id").is(filter.getCompanyUUID());

            if (filter.getRequiredStatus() != null) {
                criteria = criteria.and("invoiceStatus").is(filter.getRequiredStatus());
            }

            criteriaList.add(criteria);
        }

        int x =5;
    }
}

