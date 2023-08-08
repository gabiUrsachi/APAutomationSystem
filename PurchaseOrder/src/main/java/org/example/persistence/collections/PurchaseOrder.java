package org.example.persistence.collections;


import com.mongodb.lang.NonNull;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.example.persistence.utils.OrderStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
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
public class PurchaseOrder {

    @Id
    @Generated
    private UUID identifier;

    @NonNull
    private UUID buyer;

    @NonNull
    private UUID seller;

    private Set<Item> items;

    private OrderStatus orderStatus;

    @Version
    private Integer version;
}
