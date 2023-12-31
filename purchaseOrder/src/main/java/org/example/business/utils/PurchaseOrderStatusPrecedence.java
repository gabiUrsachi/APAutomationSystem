package org.example.business.utils;

import org.example.persistence.utils.data.OrderStatus;

import java.util.*;

/**
 * This class is used for maintaining valid order status transitions
 */
public class PurchaseOrderStatusPrecedence {
    public static final Map<OrderStatus, OrderStatus> PREDECESSORS =
            Map.ofEntries(Map.entry(OrderStatus.CREATED, OrderStatus.CREATED),
                    Map.entry(OrderStatus.SAVED, OrderStatus.CREATED),
                    Map.entry(OrderStatus.APPROVED, OrderStatus.SAVED),
                    Map.entry(OrderStatus.REJECTED, OrderStatus.SAVED));
}
