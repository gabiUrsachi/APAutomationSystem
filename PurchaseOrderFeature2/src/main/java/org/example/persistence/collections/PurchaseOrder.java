package org.system.persistence.collections;


import com.mongodb.lang.NonNull;
import lombok.*;

import org.example.persistence.collections.Company;
import org.example.persistence.collections.Item;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.system.persistence.utils.OrderStatus;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Document
@Builder
public class PurchaseOrder {
    @Id
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
