package org.example.persistence.collections;


import com.mongodb.lang.NonNull;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.example.persistence.utils.OrderStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Document
@Builder
public class PurchaseOrder {

    @Id
    @Generated
    private UUID identifier;

    @NonNull
    private Company buyer;

    @NonNull
    private Company seller;

    private Set<Item> items;

    private OrderStatus orderStatus;

    private String deliveryTerms;

    private String paymentTerms;
}
