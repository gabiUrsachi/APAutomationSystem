package org.example.persistence.repository;

import org.example.persistence.collections.Invoice;
import org.example.persistence.utils.data.CompanyInvoiceStatusChangeMap;
import org.example.persistence.utils.data.CompanyOrderStatusChangeMap;
import org.example.persistence.utils.data.InvoiceFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface InvoiceCustomRepository {

    List<Invoice> findByFilters(List<InvoiceFilter> filters);

    Invoice findByUUIDAndFilters(UUID identifier, List<InvoiceFilter> filters);

    int updateByIdentifierAndVersion(UUID identifier, Integer version, Invoice invoice);

    Float getPaidAmountForLastNMonths(UUID buyerId, UUID sellerId, int monthsNumber);

    List<Invoice> findByBuyerUUIDAndDate(UUID companyUUID, Date date, Date date1);

    Page<Invoice> findByFiltersPageable(List<InvoiceFilter> filters, Pageable pageable);

    List<Invoice> findLastMonthPaidInvoicesByBuyerUUIDAndSellerUUID(UUID buyerUUID, UUID sellerUUID);

    List<CompanyInvoiceStatusChangeMap> findStatusCountMapByDate(Date lowerTimestamp, Date upperTimestamp);
}
