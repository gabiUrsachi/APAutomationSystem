package org.example.persistence.collections;


import com.mongodb.lang.NonNull;
import lombok.*;
import org.example.persistence.utils.OrderStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private OrderStatus orderStatus;

    private Integer version;
}
