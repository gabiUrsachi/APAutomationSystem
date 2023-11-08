package org.example.persistence.repository;

import org.example.persistence.collections.Invoice;
import org.example.persistence.utils.data.InvoiceFilter;

import java.util.List;
import java.util.UUID;

public interface InvoiceCustomRepository {

    List<Invoice> findByFilters(List<InvoiceFilter> filters);

    Invoice findByUUIDAndFilters(UUID identifier, List<InvoiceFilter> filters);

    int updateByIdentifierAndVersion(UUID identifier, Integer version, Invoice invoice);

    Float getPaidAmountForLastNMonths(UUID buyerId, int monthsNumber);
}
