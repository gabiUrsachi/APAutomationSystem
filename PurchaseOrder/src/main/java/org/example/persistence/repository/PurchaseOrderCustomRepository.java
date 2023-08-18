package org.example.persistence.repository;

import org.example.persistence.collections.PurchaseOrder;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface PurchaseOrderCustomRepository{
    List<PurchaseOrder> findByQuery(Query query, Class<PurchaseOrder> purchaseOrderClass);
}
