package org.system.persistence.collections;


import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.persistence.collections.Company;
import org.example.persistence.collections.Item;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

@Document
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrder {
    @Id
    private UUID identifier;

    @NonNull
    private Company buyer;

    @NonNull
    private Company seller;

    private Set<Item> items;

    private String deliveryTerms;

    private String paymentTerms;
}
