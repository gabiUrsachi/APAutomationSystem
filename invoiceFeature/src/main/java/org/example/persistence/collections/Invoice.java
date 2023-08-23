package org.example.persistence.collections;


import com.mongodb.lang.NonNull;
import lombok.*;
import org.example.persistence.utils.InvoiceStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

@Document
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    private UUID identifier;

    private UUID buyerId;

    private UUID sellerId;

    private Set<Item> items;

    private Float taxes;

    private Float totalAmount;

    private InvoiceStatus invoiceStatus;

    private Integer version;
}
