package org.example.presentation.view;

import com.mongodb.lang.NonNull;
import lombok.*;
import org.example.persistence.collections.Item;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderRequestDTO {
    @NonNull
    private UUID buyer;

    @NonNull
    private UUID seller;

    @NonNull
    private Set<Item> items;

    @NonNull
    private Integer version;
}
