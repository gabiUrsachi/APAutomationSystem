package org.example.presentation.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.business.models.CompanyDTO;
import org.example.persistence.collections.Item;
import org.example.persistence.utils.OrderStatus;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private UUID identifier;

    private CompanyDTO buyer;

    private CompanyDTO seller;

    private Set<Item> items;

    private OrderStatus orderStatus;
}
