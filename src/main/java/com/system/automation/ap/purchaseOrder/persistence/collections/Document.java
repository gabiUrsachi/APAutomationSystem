package com.system.automation.ap.purchaseOrder.persistence.collections;

import com.mongodb.lang.NonNull;
import com.system.automation.ap.purchaseOrder.utils.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@org.springframework.data.mongodb.core.mapping.Document
public class Document {
    @Id
    private String identifier;

    @NonNull
    private DocumentType documentType;

    @NonNull
    private Company buyer;

    @NonNull
    private Company seller;

    private Set<Item> items;

    private Float taxes;

    private Float totalAmount;

    private String deliveryTerms;

    private String paymentTerms;
}
