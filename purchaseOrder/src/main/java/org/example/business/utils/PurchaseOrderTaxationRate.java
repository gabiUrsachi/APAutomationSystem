package org.example.business.utils;

import org.example.persistence.utils.data.OrderStatus;

import java.util.Map;

public class PurchaseOrderTaxationRate {

    public static final Map<OrderStatus, Float> purchaseOrderTaxRate = Map.of(
            OrderStatus.CREATED, 0.0f,
            OrderStatus.SAVED, 5.0f,
            OrderStatus.APPROVED, 10.0f,
            OrderStatus.REJECTED, 10.0f
    );
}
