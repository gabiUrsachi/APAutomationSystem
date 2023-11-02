package org.example.persistence.collections;


import com.mongodb.lang.NonNull;
import lombok.*;
import org.example.persistence.utils.data.OrderHistoryObject;
import org.example.persistence.utils.data.OrderStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * The structure of a document representing purchase orders
 */
@Getter
@Setter
@Document
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PurchaseOrder {

    @Id
    @Generated
    @EqualsAndHashCode.Include()
    private UUID identifier;

    @NonNull
    private UUID buyer;

    @NonNull
    private UUID seller;

    private Set<Item> items;

    private List<OrderHistoryObject> statusHistory;

    private Integer version;

    private String uri;
}
