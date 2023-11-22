package org.example.persistence.utils.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.persistence.collections.PurchaseOrder;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedPurchaseOrders {
    List<PurchaseOrder> content;
    Integer total;
}
