package org.example.presentation.view;

import com.mongodb.lang.NonNull;
import lombok.*;
import org.example.persistence.collections.Item;
import org.example.persistence.utils.data.OrderHistoryObject;
import org.example.persistence.utils.data.OrderStatus;
import org.junit.jupiter.api.Order;
import org.springframework.lang.Nullable;


import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderRequestDTO {
    private UUID identifier;

    @NonNull
    private UUID buyer;

    @NonNull
    private UUID seller;

    @NonNull
    private Set<Item> items;

    private OrderStatus orderStatus;

    @NonNull
    private Integer version;

    @Nullable
    private String uri;
}
