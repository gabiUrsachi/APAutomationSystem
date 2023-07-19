package org.system.business.models;

import com.mongodb.lang.NonNull;
import lombok.Data;
import org.example.persistence.collections.Company;
import org.example.persistence.collections.Item;

import java.util.Set;

@Data
public class PurchaseOrderDTO {
    @NonNull
    private Company buyer;

    @NonNull
    private Company seller;

    private Set<Item> items;
}
