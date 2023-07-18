package com.system.automation.ap.purchaseOrder.persistence.collections;

import com.mongodb.lang.NonNull;
import com.system.automation.ap.purchaseOrder.utils.DocumentType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrder {
    @Id
    private String identifier;

    @NonNull
    private DocumentType documentType;

    @NonNull
    private Company buyer;

    @NonNull
    private Company seller;

    private Set<Item> items;

    private String deliveryTerms;

    private String paymentTerms;
}
