package org.example.persistence.repository;

import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.data.OrderStatus;
import org.example.persistence.utils.data.PurchaseOrderFilter;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderCustomRepository{

    List<PurchaseOrder> findByFilters(List<PurchaseOrderFilter> filters);
    PurchaseOrder findByUUIDAndFilters(UUID identifier,List<PurchaseOrderFilter> filters);
    int updateByIdentifierAndVersionAndStatus(UUID identifier, Integer version, OrderStatus orderStatus, PurchaseOrder purchaseOrder);
}
