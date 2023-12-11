package org.example.business.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.persistence.utils.data.OrderStatus;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusTaxPair {

    private OrderStatus orderStatus;
    private Float taxAmount;
}
