package org.example.persistence.collections;


import lombok.*;
import org.example.persistence.utils.data.InvoiceStatusHistoryObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Document
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndex(def = "{'sellerId': 1, 'statusHistory.status': 1}")
@CompoundIndex(def = "{'buyerId': 1, 'statusHistory.status': 1}")
public class Invoice {
    @Id
    private UUID identifier;

    private UUID buyerId;

    private UUID sellerId;

    private Set<Item> items;

    private Float taxes;

    private Float totalAmount;

    private Float discountRate;

    List<InvoiceStatusHistoryObject> statusHistory;

    private Integer version;

    private String uri;
}
