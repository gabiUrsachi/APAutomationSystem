package org.example.persistence.utils.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusOccurrencePair {

    private OrderStatus status;
    private Integer count;
}
