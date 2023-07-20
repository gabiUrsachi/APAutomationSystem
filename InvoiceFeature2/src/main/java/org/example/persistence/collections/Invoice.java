package org.example.persistence.collections;


import com.mongodb.lang.NonNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

@Document
@Setter
@Getter
@Builder
public class Invoice {
    @Id
    private UUID identifier;

    @NonNull
    private Company buyer;

    @NonNull
    private Company seller;

    @NonNull
    private Set<Item> items;

    private Float taxes;

    private Float totalAmount;
}
