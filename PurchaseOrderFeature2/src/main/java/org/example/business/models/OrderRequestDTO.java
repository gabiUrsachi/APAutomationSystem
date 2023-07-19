package org.system.business.models;

import com.mongodb.lang.NonNull;
import lombok.Builder;
import lombok.Data;
import org.example.persistence.collections.Company;
import org.example.persistence.collections.Item;

import java.util.Set;

@Data
@Builder
public class OrderRequestDTO {
    @NonNull
    private Company buyer;

    @NonNull
    private Company seller;

    private Set<Item> items;
}
