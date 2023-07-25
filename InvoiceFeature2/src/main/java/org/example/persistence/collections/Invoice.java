package org.example.persistence.collections;


import com.mongodb.lang.NonNull;
import lombok.*;
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

    private Company buyer;

    private Company seller;

    private Set<Item> items;

    private Float taxes;

    private Float totalAmount;
}
