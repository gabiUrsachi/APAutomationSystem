package org.example.persistence.collections;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {
    private String description;

    private Integer quantity;

    private Float price;
}
