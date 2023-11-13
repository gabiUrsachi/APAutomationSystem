package org.example.presentation.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.utils.data.OrderStatus;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleOrderResponseDTO {
    private UUID identifier;

    private String buyer;

    private String seller;

    private OrderStatus orderStatus;
}