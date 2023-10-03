package org.example.presentation.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.collections.Item;
import org.example.persistence.utils.data.OrderStatus;
import org.springframework.web.multipart.MultipartFile;

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

    private Integer version;

    private MultipartFile file;
}
