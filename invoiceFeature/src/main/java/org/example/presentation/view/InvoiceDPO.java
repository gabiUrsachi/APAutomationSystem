package org.example.presentation.view;


import lombok.*;
import org.example.persistence.collections.Item;
import org.example.persistence.utils.InvoiceStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

//DPO = Data Publishing Object

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InvoiceDPO {
    private UUID identifier;

    private UUID buyerId;

    private UUID sellerId;

    private Set<Item> items;

    private Float taxes;

    private Float totalAmount;
}
