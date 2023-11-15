package org.example.persistence.repository;

import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.data.OrderStatus;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface PurchaseOrderCustomRepository{

    List<PurchaseOrder> findByFilters(List<PurchaseOrderFilter> filters);
    PurchaseOrder findByUUIDAndFilters(UUID identifier,List<PurchaseOrderFilter> filters);
    public List<PurchaseOrder> findByBuyerUUIDAndDate(UUID sellerId, Date lowerTimestamp, Date upperTimestamp);

    int updateByIdentifierAndVersion(UUID identifier, Integer version, OrderStatus orderStatus, PurchaseOrder purchaseOrder);

    Page<PurchaseOrder> findByFiltersPageable(List<PurchaseOrderFilter> filters, Pageable pageable);
}

