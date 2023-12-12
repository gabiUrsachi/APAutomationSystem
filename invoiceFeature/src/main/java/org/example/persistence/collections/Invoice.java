package org.example.persistence.collections;


import lombok.*;
import org.example.persistence.utils.data.InvoiceStatusHistoryObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
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
@CompoundIndexes({
        @CompoundIndex(name = "roleIndex", def = "{'buyerId' : 1, 'sellerId': 1}")
})
public class Invoice {
    @Id
    private UUID identifier;

    @Indexed
    private UUID buyerId;

    @Indexed
    private UUID sellerId;

    private Set<Item> items;

    private Float taxes;

    private Float totalAmount;

    private Float discountRate;

    List<InvoiceStatusHistoryObject> statusHistory;

    private Integer version;

    private String uri;
}
