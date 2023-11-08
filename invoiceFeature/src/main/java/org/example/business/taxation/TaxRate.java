package org.example.business.taxation;

import org.example.persistence.utils.data.OrderStatus;

import java.util.Map;

public class TaxRate {
    public static final Map<OrderStatus,Integer> taxRate = Map.of(
            OrderStatus.CREATED,0,
            OrderStatus.SAVED,5,
            OrderStatus.APPROVED,10,
            OrderStatus.REJECTED,10
    );
}
