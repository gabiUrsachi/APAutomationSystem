package org.example.persistence.collections;

import lombok.Data;

@Data
public class Item {
    private String description;

    private Integer quantity;

    private Float price;
}
